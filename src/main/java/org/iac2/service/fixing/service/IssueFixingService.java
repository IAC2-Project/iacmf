package org.iac2.service.fixing.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iac2.common.exception.IssueTypeNotMappedException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.compliancejob.issue.IssueFixingReportEntity;
import org.iac2.entity.plugin.PluginUsageEntity;
import org.iac2.repository.compliancejob.IssueFixingConfigurationRepository;
import org.iac2.repository.compliancejob.IssueFixingReportRepository;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.iac2.service.fixing.common.model.IssueFixingReport;
import org.iac2.service.fixing.plugin.factory.IssueFixingPluginFactory;
import org.iac2.service.utility.EntityToPojo;
import org.iac2.service.utility.PluginConfigurationHelperService;
import org.iac2.service.utility.PojoToEntity;
import org.springframework.stereotype.Service;

@Service
public class IssueFixingService {
    private final IssueFixingPluginFactory pluginManager;
    private final IssueFixingReportRepository issueFixingReportRepository;

    private final IssueFixingConfigurationRepository issueFixingConfigurationRepository;

    private final PluginConfigurationHelperService helperService;

    public IssueFixingService(IssueFixingPluginFactory pluginManager,
                              IssueFixingReportRepository issueFixingReportRepository,
                              PluginConfigurationHelperService helperService,
                              IssueFixingConfigurationRepository issueFixingConfigurationRepository
    ) {
        this.pluginManager = pluginManager;
        this.issueFixingReportRepository = issueFixingReportRepository;
        this.helperService = helperService;
        this.issueFixingConfigurationRepository = issueFixingConfigurationRepository;
    }

    private IssueFixingReportEntity fixSingleIssue(ExecutionEntity execution, InstanceModel instanceModel, IssueFixingPlugin plugin, ComplianceIssueEntity issueE) {
        ComplianceIssue issue = EntityToPojo.transformIssue(issueE);
        ProductionSystem productionSystem =
                EntityToPojo.transformProductionSystemEntity(execution.getComplianceJob().getProductionSystem());
        IssueFixingReport report = plugin.fixIssue(issue, instanceModel, productionSystem);
        IssueFixingReportEntity reportEntity = PojoToEntity.transformFixingReport(report, issueE);

        return issueFixingReportRepository.save(reportEntity);
    }

    /**
     * Fixes the first issue in the list of issues reported in the execution
     *
     * @param execution     the current execution entity
     * @param instanceModel the system model resulting from architectural reconstruction
     * @return A report on the fixing attempt.
     */
    public IssueFixingReportEntity fixFirstIssue(ExecutionEntity execution, InstanceModel instanceModel) throws IssueTypeNotMappedException {

        List<ComplianceIssueEntity> issues = execution.getComplianceIssueEntities();

        if (issues.isEmpty()) {
            throw new IllegalArgumentException("Cannot fix issue. No issues found for the execution!");
        }

        ComplianceIssueEntity issueE = issues.get(0);
        IssueFixingPlugin plugin = this.instantiatePlugin(issueE);

        return fixSingleIssue(execution, instanceModel, plugin, issueE);
    }

    /**
     * Fixes all issues in the list of issues reported in the execution
     *
     * @param execution     the current execution entity
     * @param instanceModel the system model resulting from architectural reconstruction
     * @return A map that associates every issue with a report on the fixing attempt.
     */
    public Map<ComplianceIssueEntity, IssueFixingReportEntity> fixAllIssues(ExecutionEntity execution, InstanceModel instanceModel) throws IssueTypeNotMappedException {
        List<ComplianceIssueEntity> issues = execution.getComplianceIssueEntities();

        if (issues.isEmpty()) {
            throw new IllegalArgumentException("Cannot fix issues. No issues found for the execution!");
        }

        Map<ComplianceIssueEntity, IssueFixingReportEntity> result = new HashMap<>();

        for (ComplianceIssueEntity i : issues) {
            IssueFixingPlugin plugin = instantiatePlugin(i);
            result.put(i, fixSingleIssue(execution, instanceModel, plugin, i));
        }

        return result;
    }

    private IssueFixingPlugin instantiatePlugin(ComplianceIssueEntity entity) throws IssueTypeNotMappedException {
        final String issueType = entity.getType();
        ComplianceJobEntity complianceJob = entity.getExecution().getComplianceJob();
        PluginUsageEntity usageEntity = this.issueFixingConfigurationRepository.findByComplianceJob(complianceJob)
                .stream()
                .filter(c -> c.getIssueType().equals(issueType))
                .findFirst()
                .orElseThrow(() -> new IssueTypeNotMappedException(issueType))
                .getPluginUsage();

        return (IssueFixingPlugin) helperService.instantiatePlugin(usageEntity, entity.getExecution(), this.pluginManager);
    }
}
