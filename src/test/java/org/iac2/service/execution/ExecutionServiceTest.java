package org.iac2.service.execution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.edmm.model.DeploymentModel;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancejob.execution.ExecutionStatus;
import org.iac2.common.model.compliancejob.execution.ExecutionStep;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.compliancejob.issue.IssueFixingReportEntity;
import org.iac2.entity.compliancejob.trigger.TriggerEntity;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.plugin.architecturereconstruction.ModelEnhancementStrategyEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.repository.compliancejob.ComplianceIssueRepository;
import org.iac2.repository.compliancejob.ComplianceJobRepository;
import org.iac2.repository.compliancejob.ExecutionRepository;
import org.iac2.repository.compliancejob.IssueFixingReportRepository;
import org.iac2.repository.compliancejob.TriggerRepository;
import org.iac2.repository.compliancerule.ComplianceRuleRepository;
import org.iac2.repository.productionsystem.ProductionSystemRepository;
import org.iac2.service.architecturereconstruction.service.ArchitectureReconstructionService;
import org.iac2.service.checking.service.ComplianceRuleCheckingService;
import org.iac2.service.fixing.service.IssueFixingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ExecutionServiceTest {

    @MockBean
    private ArchitectureReconstructionService arService;
    @MockBean
    private ComplianceRuleCheckingService checkingService;
    @MockBean
    private IssueFixingService fixingService;

    @Autowired
    private ExecutionService service;
    @Autowired
    private ProductionSystemRepository productionSystemRepository;
    @Autowired
    private ComplianceRuleRepository complianceRuleRepository;
    @Autowired
    private ModelEnhancementStrategyRepository modelEnhancementStrategyRepository;
    @Autowired
    private ComplianceJobRepository complianceJobRepository;
    @Autowired
    private ExecutionRepository executionRepository;
    @Autowired
    private TriggerRepository triggerRepository;
    @Autowired
    private ComplianceIssueRepository complianceIssueRepository;
    @Autowired
    private IssueFixingReportRepository issueFixingReportRepository;

    @Test
    void testReconstruction() throws IOException {
        ClassPathResource iRes = new ClassPathResource("edmm/realworld_application_instance_model_docker_refined.yaml");
        InstanceModel instanceModel = new InstanceModel(DeploymentModel.of(iRes.getFile()));
        Mockito.when(arService.crteateInstanceModel(any())).thenReturn(instanceModel);
        Mockito.doNothing().when(arService).refineInstanceModel(any(), any());

        ComplianceJobEntity job = this.createDummyComplianceJob();
        // happy path
        ExecutionEntity execution = this.service.createNewExecution(job);
        execution = executionRepository.findById(execution.getId()).orElseThrow();
        Assertions.assertEquals(ExecutionStatus.CREATED, execution.getStatus());
        Assertions.assertEquals(ExecutionStep.START, execution.getCurrentStep());
        this.service.reconstructArchitecture(execution);
        execution = executionRepository.findById(execution.getId()).orElseThrow();
        Assertions.assertEquals(ExecutionStatus.IDLE, execution.getStatus());
        Assertions.assertEquals(ExecutionStep.RECONSTRUCTION, execution.getCurrentStep());
        Assertions.assertNotNull(execution.getInstanceModel());

        // exception
        Mockito.doThrow(new RuntimeException("fake error")).when(arService).crteateInstanceModel(any());
        execution = this.service.createNewExecution(job);
        execution = executionRepository.findById(execution.getId()).orElseThrow();
        Assertions.assertEquals(ExecutionStatus.CREATED, execution.getStatus());
        Assertions.assertEquals(ExecutionStep.START, execution.getCurrentStep());
        final ExecutionEntity execution2 = execution;
        Assertions.assertThrows(RuntimeException.class, () -> this.service.reconstructArchitecture(execution2));
        execution = executionRepository.findById(execution.getId()).orElseThrow();
        Assertions.assertEquals(ExecutionStatus.EXCEPTION, execution.getStatus());
        Assertions.assertEquals(ExecutionStep.END, execution.getCurrentStep());
        Assertions.assertEquals("", execution.getInstanceModel());
    }

    @Test
    void testChecking() throws IOException {
        ClassPathResource iRes = new ClassPathResource("edmm/realworld_application_instance_model_docker_refined.yaml");
        InstanceModel instanceModel = new InstanceModel(DeploymentModel.of(iRes.getFile()));
        ComplianceJobEntity job = this.createDummyComplianceJob();

        // no violations
        Mockito.when(checkingService.findIssuesOfSystemModel(any(), any())).thenReturn(new ArrayList<>());
        ExecutionEntity execution = this.service.createNewExecution(job);
        Collection<ComplianceIssueEntity> issues = this.service.checkCompliance(execution, instanceModel);
        execution = executionRepository.findById(execution.getId()).orElseThrow();
        Assertions.assertNotNull(issues);
        Assertions.assertTrue(issues.isEmpty());
        Assertions.assertTrue(execution.getComplianceIssueEntities().isEmpty());
        Assertions.assertEquals(ExecutionStatus.SUCCESS, execution.getStatus());
        Assertions.assertEquals(ExecutionStep.END, execution.getCurrentStep());
        Assertions.assertNotNull(execution.getEndTime());
        Assertions.assertFalse(execution.getViolationsDetected());

        // violations
        execution = this.service.createNewExecution(job);
        ComplianceIssueEntity issue = new ComplianceIssueEntity(execution, "a terrible problem", "iss1");
        Mockito.when(checkingService.findIssuesOfSystemModel(any(), any())).thenReturn(List.of(issue));
        issues = this.service.checkCompliance(execution, instanceModel);
        execution = executionRepository.findById(execution.getId()).orElseThrow();
        Assertions.assertEquals(1, issues.size());
        Assertions.assertEquals(1, execution.getComplianceIssueEntities().size());
        Assertions.assertTrue(complianceIssueRepository.existsById(issues.stream().findFirst().orElseThrow().getId()));
        Assertions.assertEquals(ExecutionStatus.IDLE, execution.getStatus());
        Assertions.assertEquals(ExecutionStep.CHECKING, execution.getCurrentStep());
        Assertions.assertNull(execution.getEndTime());
        Assertions.assertTrue(execution.getViolationsDetected());

        // Exception
        final ExecutionEntity executionFinal = this.service.createNewExecution(job);
        Mockito.when(checkingService.findIssuesOfSystemModel(any(), any())).thenThrow(RuntimeException.class);
        Assertions.assertThrows(RuntimeException.class, () -> this.service.checkCompliance(executionFinal, instanceModel));
        execution = executionRepository.findById(executionFinal.getId()).orElseThrow();
        Assertions.assertEquals(0, execution.getComplianceIssueEntities().size());
        Assertions.assertEquals(ExecutionStatus.EXCEPTION, execution.getStatus());
        Assertions.assertEquals(ExecutionStep.END, execution.getCurrentStep());
        Assertions.assertNotNull(execution.getEndTime());
        Assertions.assertFalse(execution.getViolationsDetected());
    }

    @Test
    void testFixing() throws IOException {
        ClassPathResource iRes = new ClassPathResource("edmm/realworld_application_instance_model_docker_refined.yaml");
        InstanceModel instanceModel = new InstanceModel(DeploymentModel.of(iRes.getFile()));
        ComplianceJobEntity job = this.createDummyComplianceJob();

        // batch mode
        ExecutionEntity execution = this.service.createNewExecution(job);
        ComplianceIssueEntity issue1 = new ComplianceIssueEntity(execution, "a terrible problem", "issT");
        ComplianceIssueEntity issue2 = new ComplianceIssueEntity(execution, "a horrible problem", "issT");
        complianceIssueRepository.save(issue1);
        complianceIssueRepository.save(issue2);
        IssueFixingReportEntity report1 = new IssueFixingReportEntity(true, issue1);
        IssueFixingReportEntity report2 = new IssueFixingReportEntity(false, issue2);
        Map<ComplianceIssueEntity, IssueFixingReportEntity> fixingReports = new HashMap<>();
        fixingReports.put(issue1, report1);
        fixingReports.put(issue2, report2);
        Mockito.when(this.fixingService.fixAllIssues(any(), any())).thenReturn(fixingReports);
        fixingReports = this.service.fixIssues(execution, instanceModel, true);
        Assertions.assertNotNull(fixingReports);
        Assertions.assertEquals(2, fixingReports.size());
        fixingReports.forEach((i, r) -> Assertions.assertTrue(issueFixingReportRepository.existsById(r.getId())));
        execution = executionRepository.findById(execution.getId()).orElseThrow();
        Assertions.assertEquals(ExecutionStep.END, execution.getCurrentStep());
        Assertions.assertEquals(ExecutionStatus.SUCCESS, execution.getStatus());
        Assertions.assertNotNull(execution.getEndTime());

        // no batch mode
        execution = this.service.createNewExecution(job);
        issue1 = new ComplianceIssueEntity(execution, "a terrible-worrisome problem", "issT");
        issue2 = new ComplianceIssueEntity(execution, "a horrible-worrisome problem", "issT");
        complianceIssueRepository.save(issue1);
        complianceIssueRepository.save(issue2);
        report1 = new IssueFixingReportEntity(true, issue1);
        Mockito.when(this.fixingService.fixFirstIssue(any(), any())).thenReturn(report1);
        fixingReports = this.service.fixIssues(execution, instanceModel, false);
        Assertions.assertNotNull(fixingReports);
        Assertions.assertEquals(1, fixingReports.size());
        fixingReports.forEach((i, r) -> Assertions.assertTrue(issueFixingReportRepository.existsById(r.getId())));
        Assertions.assertEquals(1, complianceIssueRepository.findById(issue1.getId()).orElseThrow().getFixingReports().size());
        Assertions.assertEquals(0, complianceIssueRepository.findById(issue2.getId()).orElseThrow().getFixingReports().size());
        execution = executionRepository.findById(execution.getId()).orElseThrow();
        Assertions.assertEquals(ExecutionStep.END, execution.getCurrentStep());
        Assertions.assertEquals(ExecutionStatus.SUCCESS, execution.getStatus());
        Assertions.assertNotNull(execution.getEndTime());

        // exception
        execution = this.service.createNewExecution(job);
        final ExecutionEntity eF = execution;
        Mockito.when(this.fixingService.fixFirstIssue(any(), any())).thenThrow(RuntimeException.class);
        Assertions.assertThrows(RuntimeException.class, () -> this.service.fixIssues(eF, instanceModel, false));
        Assertions.assertEquals(1, fixingReports.size());
        execution = executionRepository.findById(execution.getId()).orElseThrow();
        Assertions.assertEquals(ExecutionStep.END, execution.getCurrentStep());
        Assertions.assertEquals(ExecutionStatus.EXCEPTION, execution.getStatus());
        Assertions.assertNotNull(execution.getEndTime());
    }

    private ComplianceJobEntity createDummyComplianceJob() {
        ComplianceRuleEntity rule = new ComplianceRuleEntity("test", "test", "test");
        ProductionSystemEntity productionSystem = new ProductionSystemEntity("test",
                "opentoscacontainer",
                "dummy");
        ModelEnhancementStrategyEntity strategyEntity = new ModelEnhancementStrategyEntity(List.of("test"));
        TriggerEntity trigger = new TriggerEntity("test");
        productionSystemRepository.save(productionSystem);
        complianceRuleRepository.save(rule);
        modelEnhancementStrategyRepository.save(strategyEntity);
        triggerRepository.save(trigger);
        ComplianceJobEntity job = new ComplianceJobEntity("test",
                "test",
                "test",
                productionSystem,
                rule,
                strategyEntity,
                List.of(trigger));
        return complianceJobRepository.save(job);
    }
}