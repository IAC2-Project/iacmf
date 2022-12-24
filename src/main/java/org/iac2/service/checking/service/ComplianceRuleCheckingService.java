package org.iac2.service.checking.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.entity.compliancejob.ComplianceRuleConfigurationEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.compliancerule.parameter.ComplianceRuleParameterAssignmentEntity;
import org.iac2.entity.plugin.PluginUsageEntity;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.iac2.service.checking.plugin.manager.ComplianceRuleCheckingPluginManager;
import org.iac2.service.utility.EntityToPojo;
import org.iac2.service.utility.PluginConfigurationHelper;
import org.iac2.service.utility.PojoToEntity;
import org.springframework.stereotype.Service;

@Service
public class ComplianceRuleCheckingService {
    private final ComplianceRuleCheckingPluginManager pluginManager;

    @PersistenceContext
    EntityManager entityManager;

    public ComplianceRuleCheckingService(ComplianceRuleCheckingPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public Map<ComplianceRuleConfigurationEntity, Collection<ComplianceIssueEntity>> findViolationsOfAllComplianceRules(
            ExecutionEntity execution, InstanceModel instanceModel) {
        Map<ComplianceRuleConfigurationEntity, Collection<ComplianceIssueEntity>> result = new HashMap<>();

        for (ComplianceRuleConfigurationEntity configurationEntity : execution.getComplianceJob().getComplianceRuleConfigurations()) {
            result.put(configurationEntity, findViolationsOfComplianceRule(execution, configurationEntity, instanceModel));
        }

        return result;
    }

    public Collection<ComplianceIssueEntity> findViolationsOfComplianceRule(ExecutionEntity execution,
                                                                            ComplianceRuleConfigurationEntity complianceRuleConfiguration,
                                                                            InstanceModel instanceModel) {
        ComplianceRuleEntity complianceRule = complianceRuleConfiguration.getComplianceRule();
        Collection<ComplianceRuleParameterAssignmentEntity> assignments =
                complianceRuleConfiguration.getComplianceRuleParameterAssignments();
        ComplianceRule myCR = EntityToPojo.transformComplianceRule(complianceRule, assignments);
        PluginUsageEntity usageEntity = execution.getComplianceJob().getCheckingPluginUsage();
        ComplianceRuleCheckingPlugin plugin = (ComplianceRuleCheckingPlugin) PluginConfigurationHelper.instantiatePlugin(usageEntity, execution, entityManager, pluginManager);

        return plugin.findIssues(instanceModel, myCR)
                .stream()
                .map(i -> PojoToEntity.transformComplianceIssue(execution, i))
                .toList();
    }

    public ComplianceRuleCheckingPluginManager getPluginManager() {
        return this.pluginManager;
    }
}
