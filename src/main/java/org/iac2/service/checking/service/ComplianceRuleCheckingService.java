package org.iac2.service.checking.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.entity.compliancejob.ComplianceRuleConfigurationEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.plugin.PluginUsageEntity;
import org.iac2.repository.compliancejob.ComplianceIssueRepository;
import org.iac2.repository.productionsystem.KVRepository;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.iac2.service.checking.plugin.factory.ComplianceRuleCheckingPluginFactory;
import org.iac2.service.utility.EntityToPojo;
import org.iac2.service.utility.PluginConfigurationHelperService;
import org.iac2.service.utility.PojoToEntity;
import org.springframework.stereotype.Service;

@Service
public class ComplianceRuleCheckingService {
    private final ComplianceRuleCheckingPluginFactory pluginManager;
    private final ComplianceIssueRepository complianceIssueRepository;
    private final KVRepository kVRepository;

    private final PluginConfigurationHelperService helperService;

    public ComplianceRuleCheckingService(ComplianceRuleCheckingPluginFactory pluginManager,
                                         ComplianceIssueRepository complianceIssueRepository,
                                         KVRepository kVRepository, PluginConfigurationHelperService helperService) {
        this.pluginManager = pluginManager;
        this.complianceIssueRepository = complianceIssueRepository;
        this.kVRepository = kVRepository;
        this.helperService = helperService;
    }

    /**
     * Finda all violations of all compliance rules of a compliance job
     *
     * @param execution     the current execution of the compliance job.
     * @param instanceModel the instance model to be checked
     * @return a map of the issues found for each compliance rule in the job (issues are already persisted).
     */
    public Map<ComplianceRuleConfigurationEntity, Collection<ComplianceIssueEntity>> findViolationsOfAllComplianceRules(
            ExecutionEntity execution, InstanceModel instanceModel) {
        Map<ComplianceRuleConfigurationEntity, Collection<ComplianceIssueEntity>> result = new HashMap<>();
        Collection<ComplianceIssueEntity> currentIssues;

        for (ComplianceRuleConfigurationEntity configurationEntity : execution.getComplianceJob().getComplianceRuleConfigurations()) {
            currentIssues = findViolationsOfComplianceRule(execution, configurationEntity, instanceModel);

            if (currentIssues != null && !currentIssues.isEmpty()) {
                result.put(configurationEntity, currentIssues);
            }
        }

        return result;
    }

    /**
     * Finds all violations of a compliance rule. Persists the found issues.
     *
     * @param execution                   the current execution
     * @param complianceRuleConfiguration the configuration of the compliance rule to be checked against.
     * @param instanceModel               the instance model to be checked.
     * @return a list of persisted issue entities.
     */
    public Collection<ComplianceIssueEntity> findViolationsOfComplianceRule(ExecutionEntity execution,
                                                                            ComplianceRuleConfigurationEntity complianceRuleConfiguration,
                                                                            InstanceModel instanceModel) {
        ComplianceRule myCR = EntityToPojo.transformComplianceRule(complianceRuleConfiguration);
        PluginUsageEntity usageEntity = execution.getComplianceJob().getCheckingPluginUsage();
        ComplianceRuleCheckingPlugin plugin = (ComplianceRuleCheckingPlugin) helperService.instantiatePlugin(usageEntity, execution, pluginManager);
        Collection<ComplianceIssue> issues = plugin.findIssues(instanceModel, myCR);
        Collection<ComplianceIssueEntity> result = issues.stream()
                .map(issue -> {
                    ComplianceIssueEntity entity = PojoToEntity.transformComplianceIssue(execution, issue);
                    ComplianceIssueEntity loaded = complianceIssueRepository.save(entity);
                    kVRepository.saveAll(entity.getProperties());

                    return loaded;
                })
                .toList();

        return result;
    }

    public ComplianceRuleCheckingPluginFactory getPluginManager() {
        return this.pluginManager;
    }
}
