package org.iac2.checking.service;

import java.util.Collection;

import org.iac2.checking.common.exception.ComplianceRuleTypeNotSupportedException;
import org.iac2.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.iac2.checking.common.model.compliancerule.ComplianceRule;
import org.iac2.checking.plugin.manager.ComplianceRuleCheckingPluginManager;
import org.iac2.common.model.SystemModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.compliancerule.parameter.assignment.ComplianceRuleParameterAssignmentEntity;
import org.iac2.entity.compliancerule.parameter.assignment.IntegerParameterAssignmentEntity;
import org.iac2.entity.compliancerule.parameter.assignment.StringParameterAssignmentEntity;
import org.springframework.stereotype.Service;

@Service
public class ComplianceRuleCheckingService {
    private final ComplianceRuleCheckingPluginManager pluginManager;

    public ComplianceRuleCheckingService(ComplianceRuleCheckingPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public Collection<ComplianceIssue> findIssuesOfSystemModel(ComplianceJobEntity complianceJob, SystemModel systemModel) {
        return this.findIssuesOfSystemModel(
                complianceJob.getComplianceRule(),
                complianceJob.getComplianceRuleParameterAssignments(),
                systemModel);
    }

    public Collection<ComplianceIssue> findIssuesOfSystemModel(
            ComplianceRuleEntity complianceRule,
            Collection<ComplianceRuleParameterAssignmentEntity> assignments,
            SystemModel systemModel) {
        ComplianceRule myCR = this.transformComplianceRule(complianceRule, assignments);
        Collection<ComplianceRuleCheckingPlugin> plugins = this.pluginManager.getPossiblePluginsForComplianceRule(myCR);

        if(plugins.size() == 0) {
            throw new ComplianceRuleTypeNotSupportedException(myCR.getType());
        }

        ComplianceRuleCheckingPlugin plugin = plugins.stream().findFirst().get();
        return plugin.findIssues(systemModel, myCR);
    }

    private ComplianceRule transformComplianceRule(ComplianceRuleEntity complianceRule,
                                                   Collection<ComplianceRuleParameterAssignmentEntity> assignments) {
        ComplianceRule myCR = new ComplianceRule(complianceRule.getType(), complianceRule.getLocation());
        assignments.forEach(assignment -> {
            if (assignment instanceof IntegerParameterAssignmentEntity) {
                myCR.addIntParameter(assignment.getParameter().getName(),
                        ((IntegerParameterAssignmentEntity) assignment).getIntValue());
            } else if (assignment instanceof StringParameterAssignmentEntity) {
                myCR.addStringParameter(assignment.getParameter().getName(),
                        ((StringParameterAssignmentEntity) assignment).getStringValue());
            }
        });

        return myCR;
    }

}
