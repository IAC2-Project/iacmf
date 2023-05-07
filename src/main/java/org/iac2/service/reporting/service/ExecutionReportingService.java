package org.iac2.service.reporting.service;

import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.compliancejob.issue.IssueFixingReportEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.service.fixing.plugin.factory.IssueFixingPluginFactory;
import org.iac2.service.reporting.plugin.factory.ReportingPluginFactory;
import org.iac2.service.utility.PluginConfigurationHelperService;
import org.springframework.stereotype.Service;

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

    public void ReportExecution(ExecutionEntity execution, ProductionSystemEntity productionSystem, Map<ComplianceIssueEntity, IssueFixingReportEntity> fixingReports){


    }
}
