package org.iac2.service.execution;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancejob.execution.ExecutionStatus;
import org.iac2.common.model.compliancejob.execution.ExecutionStep;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.compliancejob.issue.IssueFixingReportEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.repository.compliancejob.ComplianceIssueRepository;
import org.iac2.repository.compliancejob.ExecutionRepository;
import org.iac2.repository.compliancejob.IssueFixingReportRepository;
import org.iac2.service.architecturereconstruction.service.ArchitectureReconstructionService;
import org.iac2.service.checking.service.ComplianceRuleCheckingService;
import org.iac2.service.fixing.service.IssueFixingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ExecutionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionService.class);
    private final ExecutionRepository executionRepository;
    private final ArchitectureReconstructionService architectureReconstructionService;
    private final ComplianceRuleCheckingService checkingService;
    private final IssueFixingService fixingService;
    private final ComplianceIssueRepository complianceIssueRepository;
    private final IssueFixingReportRepository issueFixingReportRepository;

    public ExecutionService(ExecutionRepository executionRepository,
                            ArchitectureReconstructionService architectureReconstructionService,
                            ComplianceRuleCheckingService checkingService,
                            IssueFixingService fixingService,
                            ComplianceIssueRepository complianceIssueRepository,
                            IssueFixingReportRepository issueFixingReportRepository) {
        this.executionRepository = executionRepository;
        this.architectureReconstructionService = architectureReconstructionService;
        this.checkingService = checkingService;
        this.fixingService = fixingService;
        this.complianceIssueRepository = complianceIssueRepository;
        this.issueFixingReportRepository = issueFixingReportRepository;
    }

    @Async
    public void runComplianceJobExecution(@NotNull ExecutionEntity execution, boolean isBatchFixing) {
        InstanceModel instanceModel = this.reconstructArchitecture(execution);
        Collection<ComplianceIssueEntity> issues = this.checkCompliance(execution, instanceModel);

        if (!issues.isEmpty()) {
            Map<ComplianceIssueEntity, IssueFixingReportEntity> reports =
                    this.fixIssues(execution, instanceModel, isBatchFixing);
        }
        // todo extend with validation and reporting
    }

    public ExecutionEntity createNewExecution(@NotNull ComplianceJobEntity complianceJob) {
        ExecutionEntity execution = new ExecutionEntity(complianceJob);
        execution = this.executionRepository.save(execution);
        LOGGER.info("Execution with id {} for job {} was created.", execution.getId(), complianceJob.getId());

        return execution;
    }

    public InstanceModel reconstructArchitecture(ExecutionEntity execution) {
        execution.setStatus(ExecutionStatus.RUNNING);
        execution.setCurrentStep(ExecutionStep.RECONSTRUCTION);
        executionRepository.save(execution);
        LOGGER.info("Starting execution with id {} for job {}...", execution.getId(), execution.getComplianceJob().getId());
        LOGGER.info("Reconstructing the architecture (execution id: {}, job id: {})...", execution.getId(), execution.getComplianceJob().getId());
        try {
            ProductionSystemEntity productionSystem = execution.getComplianceJob().getProductionSystem();
            InstanceModel result = this.architectureReconstructionService.crteateInstanceModel(productionSystem);
            StringWriter writer = new StringWriter();
            result.getDeploymentModel().getGraph().generateYamlOutput(writer);
            execution.setInstanceModel(writer.toString());
            this.architectureReconstructionService.refineInstanceModel(execution.getComplianceJob(), result);
            execution.setStatus(ExecutionStatus.IDLE);
            executionRepository.save(execution);
            LOGGER.info("Successfully finished the reconstruction of the architecture (execution id: {}, job id: {})...",
                    execution.getId(), execution.getComplianceJob().getId());

            return result;
        } catch (RuntimeException e) {
            throw this.handleException(e, execution);
        }
    }

    public Collection<ComplianceIssueEntity> checkCompliance(ExecutionEntity execution, InstanceModel instanceModel) {
        execution.setCurrentStep(ExecutionStep.CHECKING);
        executionRepository.save(execution);
        LOGGER.info("Checking the compliance rules (execution id: {}, job id: {})...", execution.getId(), execution.getComplianceJob().getId());
        try {
            Collection<ComplianceIssueEntity> issues = this.checkingService.findIssuesOfSystemModel(execution, instanceModel);
            LOGGER.info("Finished checking the compliance rules (execution id: {}, job id: {}).", execution.getId(), execution.getComplianceJob().getId());

            if (issues.isEmpty()) {
                this.endExecution(execution, false,
                        "The compliance job execution finished without errors. No compliance issues were detected.");
            } else {
                execution.setViolationsDetected(true);
                this.complianceIssueRepository.saveAll(issues);
                execution.setStatus(ExecutionStatus.IDLE);
                executionRepository.save(execution);
            }

            return issues;
        } catch (RuntimeException e) {
            throw this.handleException(e, execution);
        }
    }

    public Map<ComplianceIssueEntity, IssueFixingReportEntity> fixIssues(ExecutionEntity execution, InstanceModel instanceModel, boolean isBatchFix) {
        execution.setCurrentStep(ExecutionStep.FIXING);
        executionRepository.save(execution);
        LOGGER.info("Fixing the detected compliance rule violations (execution id: {}, job id: {})...",
                execution.getId(), execution.getComplianceJob().getId());
        try {
            Map<ComplianceIssueEntity, IssueFixingReportEntity> result = new HashMap<>();

            if (isBatchFix) {
                result.putAll(this.fixingService.fixAllIssues(execution, instanceModel));
            } else {
                IssueFixingReportEntity report = this.fixingService.fixFirstIssue(execution, instanceModel);
                result.put(report.getComplianceIssue(), report);
            }

            issueFixingReportRepository.saveAll(result.values());

            LOGGER.info("Finished fixing the detected compliance rule violations (execution id: {}, job id: {}).",
                    execution.getId(), execution.getComplianceJob().getId());

            // todo change when additional steps are implemented
            this.endExecution(execution, false,
                    "The compliance job execution finished without errors . Issues were found and attempted to be fixed.");

            return result;
        } catch (RuntimeException e) {
            throw this.handleException(e, execution);
        }
    }

    private RuntimeException handleException(RuntimeException e, ExecutionEntity execution) {
        String message = String.format("The execution with id: (%s) has errored at step: (%s). Reason: %s",
                execution.getId(), execution.getCurrentStep(), e.getMessage());
        endExecution(execution, true, message);
        return e;
    }

    private void endExecution(ExecutionEntity execution, boolean isFailed, String description) {
        execution.setStatus(isFailed ? ExecutionStatus.EXCEPTION : ExecutionStatus.SUCCESS);
        execution.setEndTime(new Date());
        execution.setCurrentStep(ExecutionStep.END);
        execution.setDescription(description);
        executionRepository.save(execution);

        if (isFailed) {
            LOGGER.error(description);
        } else {
            LOGGER.info(description);
        }
    }
}