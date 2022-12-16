package org.iac2.service.fixing.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.compliancejob.issue.IssueFixingReportEntity;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.iac2.service.fixing.common.model.IssueFixingReport;
import org.iac2.service.fixing.plugin.manager.IssueFixingPluginManager;
import org.iac2.service.utility.EntityToPojo;
import org.iac2.service.utility.PojoToEntity;
import org.springframework.stereotype.Service;

@Service
public class IssueFixingService {
    private final IssueFixingPluginManager pluginManager;

    public IssueFixingService(IssueFixingPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    /**
     * Fixes the first issue in the list of issues reported in the execution
     *
     * @param execution     the current execution entity
     * @param instanceModel the system model resulting from architectural reconstruction
     * @return A report on the fixing attempt.
     */
    public IssueFixingReportEntity fixFirstIssue(ExecutionEntity execution, InstanceModel instanceModel) {
        String pluginId = execution.getComplianceJob().getModelFixingPluginId();
        IssueFixingPlugin plugin = this.pluginManager.getPlugin(pluginId);
        List<ComplianceIssueEntity> issues = execution.getComplianceIssueEntities();

        if (issues.isEmpty()) {
            throw new IllegalArgumentException("Cannot fix issue. No issues found for the execution!");
        }

        ComplianceIssueEntity issueE = issues.get(0);

        return fixSingleIssue(execution, instanceModel, plugin, issueE);
    }

    /**
     * Fixes all issues in the list of issues reported in the execution
     *
     * @param execution     the current execution entity
     * @param instanceModel the system model resulting from architectural reconstruction
     * @return A map that associates every issue with a report on the fixing attempt.
     */
    public Map<ComplianceIssueEntity, IssueFixingReportEntity> fixAllIssues(ExecutionEntity execution, InstanceModel instanceModel) {
        String pluginId = execution.getComplianceJob().getModelFixingPluginId();
        IssueFixingPlugin plugin = this.pluginManager.getPlugin(pluginId);
        List<ComplianceIssueEntity> issues = execution.getComplianceIssueEntities();

        if (issues.isEmpty()) {
            throw new IllegalArgumentException("Cannot fix issue. No issues found for the execution!");
        }

        Map<ComplianceIssueEntity, IssueFixingReportEntity> result = new HashMap<>();
        issues.forEach(i -> result.put(i, fixSingleIssue(execution, instanceModel, plugin, i)));

        return result;
    }

    private static IssueFixingReportEntity fixSingleIssue(ExecutionEntity execution, InstanceModel instanceModel, IssueFixingPlugin plugin, ComplianceIssueEntity issueE) {
        ComplianceIssue issue = EntityToPojo.transformIssue(issueE);
        ProductionSystem productionSystem =
                EntityToPojo.transformProductionSystemEntity(execution.getComplianceJob().getProductionSystem());
        IssueFixingReport report = plugin.fixIssue(issue, instanceModel, productionSystem);

        return PojoToEntity.transformFixingReport(report, issueE);
    }
}
