package org.iac2.service.fixing.service;

import javax.transaction.Transactional;

import org.iac2.common.model.InstanceModel;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.ComplianceRuleConfigurationEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.compliancejob.issue.IssueFixingConfigurationEntity;
import org.iac2.entity.compliancejob.issue.IssueFixingReportEntity;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.plugin.PluginUsageEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.repository.compliancejob.ComplianceIssueRepository;
import org.iac2.repository.compliancejob.ComplianceJobRepository;
import org.iac2.repository.compliancejob.ComplianceRuleConfigurationRepository;
import org.iac2.repository.compliancejob.ExecutionRepository;
import org.iac2.repository.compliancejob.IssueFixingConfigurationRepository;
import org.iac2.repository.compliancerule.ComplianceRuleRepository;
import org.iac2.repository.plugin.PluginUsageRepository;
import org.iac2.repository.productionsystem.ProductionSystemRepository;
import org.iac2.service.fixing.plugin.factory.IssueFixingPluginFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
class IssueFixingServiceTest {

    @MockBean
    IssueFixingPluginFactory pluginManager;

    @Autowired
    IssueFixingService service;
    @Autowired
    private ExecutionRepository executionRepository;
    @Autowired
    private ComplianceIssueRepository complianceIssueRepository;
    @Autowired
    private ComplianceJobRepository complianceJobRepository;
    @Autowired
    private PluginUsageRepository pluginUsageRepository;
    @Autowired
    private ProductionSystemRepository productionSystemRepository;
    @Autowired
    private ComplianceRuleRepository complianceRuleRepository;
    @Autowired
    private ComplianceRuleConfigurationRepository complianceRuleConfigurationRepository;
    @Autowired
    private IssueFixingConfigurationRepository issueFixingConfigurationRepository;

    @Test
    void fixFirstIssue() {
        Mockito.when(pluginManager.createPlugin(anyString()))
                .thenReturn(new MockIssueFixingPlugin());

        ComplianceRuleEntity complianceRule = new ComplianceRuleEntity(
                "ensure-property-value",
                "",
                "my useless compliance rule!"
        );
        complianceRuleRepository.save(complianceRule);
        PluginUsageEntity usageEntity = new PluginUsageEntity("opentosca-container-model-creation-plugin");
        PluginUsageEntity checkingEntity = new PluginUsageEntity("checking");
        pluginUsageRepository.save(usageEntity);
        pluginUsageRepository.save(checkingEntity);
        ProductionSystemEntity productionSystem = new ProductionSystemEntity(
                "my super system",
                "opentoscacontainer",
                usageEntity);
        productionSystemRepository.save(productionSystem);
        ComplianceJobEntity complianceJob = new ComplianceJobEntity(
                "my super job", productionSystem, checkingEntity);
        complianceJobRepository.save(complianceJob);
        ComplianceRuleConfigurationEntity crConfigEntity = new ComplianceRuleConfigurationEntity(complianceRule, complianceJob, "wrong-property-value");
        complianceRuleConfigurationRepository.save(crConfigEntity);
        ExecutionEntity execution = new ExecutionEntity(complianceJob);
        executionRepository.save(execution);
        PluginUsageEntity fixingPluginUsage = new PluginUsageEntity("bla bla");
        pluginUsageRepository.save(fixingPluginUsage);
        IssueFixingConfigurationEntity issueFixingConfiguration = new IssueFixingConfigurationEntity("wrong-property-value", complianceJob, fixingPluginUsage);
        issueFixingConfigurationRepository.save(issueFixingConfiguration);
        InstanceModel instanceModel = new InstanceModel(null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.fixFirstIssue(execution, instanceModel));
        ComplianceIssueEntity issue = new ComplianceIssueEntity(crConfigEntity, execution, "something is wrong!", "wrong-property-value");
        complianceIssueRepository.save(issue);
        IssueFixingReportEntity report = service.fixFirstIssue(execution, instanceModel);
        Assertions.assertNotNull(report);
        Assertions.assertTrue(report.getIsSuccessful());
        Assertions.assertEquals("", report.getDescription());
    }
}
