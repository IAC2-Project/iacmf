package org.iac2.service.reporting.service;

import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.execution.Execution;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancejob.issue.IssueFixingReport;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.compliancejob.issue.IssueFixingReportEntity;
import org.iac2.entity.plugin.PluginUsageEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.service.reporting.common.interfaces.ReportingPlugin;
import org.iac2.service.reporting.common.model.ExecutionReport;
import org.iac2.service.reporting.plugin.factory.ReportingPluginFactory;
import org.iac2.service.utility.EntityToPojo;
import org.iac2.service.utility.PluginConfigurationHelperService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class ExecutionReportingService {
    private final ReportingPluginFactory pluginManager;
    private final PluginConfigurationHelperService helperService;

    public ExecutionReportingService(ReportingPluginFactory pluginManager,
                                     PluginConfigurationHelperService helperService) {
        this.pluginManager = pluginManager;
        this.helperService = helperService;
    }

    public void reportExecution(ExecutionEntity execution, ProductionSystemEntity productionSystem, Map<ComplianceIssueEntity, IssueFixingReportEntity> fixingReports){
        Execution executionPojo = EntityToPojo.transformExecution(execution);
        ProductionSystem productionSystemPojo = EntityToPojo.transformProductionSystemEntity(productionSystem);
        Map<ComplianceIssue, IssueFixingReport> reports = new HashMap<>();
        fixingReports.forEach((i, r) -> {
            reports.put(EntityToPojo.transformIssue(i), EntityToPojo.transformIssueFixingReport(r));
        });
        ExecutionReport executionReport = new ExecutionReport(executionPojo, reports, productionSystemPojo);
        ComplianceJobEntity complianceJob = execution.getComplianceJob();
        PluginUsageEntity pluginUsageEntity = complianceJob.getReportingPluginUsage();
        ReportingPlugin plugin = (ReportingPlugin) helperService.instantiatePlugin(pluginUsageEntity, execution, this.pluginManager);
        plugin.reportExecutionOutcome(executionReport);

    }
}
