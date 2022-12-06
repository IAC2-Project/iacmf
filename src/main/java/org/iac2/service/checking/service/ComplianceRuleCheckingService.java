package org.iac2.service.checking.service;

import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.compliancerule.parameter.assignment.ComplianceRuleParameterAssignmentEntity;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.iac2.service.checking.plugin.manager.ComplianceRuleCheckingPluginManager;
import org.iac2.service.utility.EntityToPojo;
import org.iac2.service.utility.PojoToEntity;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ComplianceRuleCheckingService {
    private final ComplianceRuleCheckingPluginManager pluginManager;

    public ComplianceRuleCheckingService(ComplianceRuleCheckingPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public Collection<ComplianceIssueEntity> findIssuesOfSystemModel(ExecutionEntity execution, InstanceModel instanceModel) {
        ComplianceRuleEntity complianceRule = execution.getComplianceJob().getComplianceRule();
        Collection<ComplianceRuleParameterAssignmentEntity> assignments =
                execution.getComplianceJob().getComplianceRuleParameterAssignments();
        ComplianceRule myCR = EntityToPojo.transformComplianceRule(complianceRule, assignments);
        ComplianceRuleCheckingPlugin plugin =
                this.pluginManager.getPlugin(execution.getComplianceJob().getModelCheckingPluginId());

        return plugin.findIssues(instanceModel, myCR)
                .stream()
                .map(i -> PojoToEntity.transformComplianceIssue(execution, i))
                .toList();
    }


}
