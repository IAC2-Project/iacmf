package org.iac2.service.checking.service;

import java.util.Collection;
import java.util.Map;

import javax.transaction.Transactional;

import org.iac2.common.model.InstanceModel;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.ComplianceRuleConfigurationEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.plugin.PluginUsageEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.repository.compliancejob.ComplianceJobRepository;
import org.iac2.repository.compliancejob.ComplianceRuleConfigurationRepository;
import org.iac2.repository.compliancejob.ExecutionRepository;
import org.iac2.repository.compliancerule.ComplianceRuleRepository;
import org.iac2.repository.plugin.PluginUsageRepository;
import org.iac2.repository.productionsystem.ProductionSystemRepository;
import org.iac2.service.checking.plugin.factory.ComplianceRuleCheckingPluginFactory;
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
class ComplianceRuleCheckingServiceTest {

    @MockBean
    ComplianceRuleCheckingPluginFactory pluginManager;

    @Autowired
    ComplianceRuleCheckingService service;
    @Autowired
    private PluginUsageRepository pluginUsageRepository;
    @Autowired
    private ProductionSystemRepository productionSystemRepository;
    @Autowired
    private ComplianceRuleConfigurationRepository complianceRuleConfigurationRepository;
    @Autowired
    private ComplianceJobRepository complianceJobRepository;
    @Autowired
    private ExecutionRepository executionRepository;
    @Autowired
    private ComplianceRuleRepository complianceRuleRepository;

    @Test
    void findIssuesOfSystemModel() {
        Mockito.when(pluginManager.createPlugin(anyString()))
                .thenReturn(new MockComplianceCheckingPlugin());

        ComplianceRuleEntity cr = new ComplianceRuleEntity(
                "cr",
                "ensure-property-value",
                "http://nowherer.no",
                "compliacne rule for fooling around");
        complianceRuleRepository.save(cr);
        PluginUsageEntity usage = new PluginUsageEntity("opentosca-container-model-creation-plugin");
        pluginUsageRepository.save(usage);
        ProductionSystemEntity productionSystem = new ProductionSystemEntity(
                "abc",
                "some system",
                "opentoscacontainer",
                usage);
        productionSystemRepository.save(productionSystem);
        PluginUsageEntity checkingUsage = new PluginUsageEntity("checking");
        pluginUsageRepository.save(checkingUsage);
        PluginUsageEntity reporting = new PluginUsageEntity("reporting");
        pluginUsageRepository.save(reporting);
        ComplianceJobEntity complianceJob = new ComplianceJobEntity("job1",
                "a fine job", productionSystem, checkingUsage, reporting);
        complianceJobRepository.save(complianceJob);
        ComplianceRuleConfigurationEntity crConfig = new ComplianceRuleConfigurationEntity(cr, complianceJob, "issueType1");
        complianceRuleConfigurationRepository.save(crConfig);
        ExecutionEntity execution = new ExecutionEntity(complianceJob);
        executionRepository.save(execution);
        Map<ComplianceRuleConfigurationEntity, Collection<ComplianceIssueEntity>> issues = service.findViolationsOfAllComplianceRules(
                execution,
                new InstanceModel(null)
        );

        Assertions.assertNotNull(issues);
        Assertions.assertEquals(1, issues.size());
        Assertions.assertNotNull(issues.get(crConfig));
        Assertions.assertEquals(2, issues.get(crConfig).size());
    }
}
