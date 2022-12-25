package org.iac2.service.fixing.service;

import org.iac2.common.model.InstanceModel;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.ComplianceRuleConfigurationEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.compliancejob.issue.IssueFixingReportEntity;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.plugin.PluginUsageEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.service.fixing.plugin.factory.IssueFixingPluginFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class IssueFixingServiceTest {

    @MockBean
    IssueFixingPluginFactory pluginManager;

    @Autowired
    IssueFixingService service;

    @Test
    void fixFirstIssue() {
        Mockito.when(pluginManager.createPlugin(anyString()))
                .thenReturn(new MockIssueFixingPlugin());

        ComplianceRuleEntity complianceRule = new ComplianceRuleEntity(
                "ensure-property-value",
                "",
                "my useless compliance rule!"
        );
        PluginUsageEntity usageEntity = new PluginUsageEntity("opentosca-container-model-creation-plugin");
        ProductionSystemEntity productionSystem = new ProductionSystemEntity(
                "my super system",
                "opentoscacontainer");
        productionSystem.setModelCreationPluginUsage(usageEntity);

        ComplianceJobEntity complianceJob = new ComplianceJobEntity(
                "my super job",
                productionSystem
        );
        ComplianceRuleConfigurationEntity crConfigEntity = new ComplianceRuleConfigurationEntity(complianceRule, "wrong-property-value");
        complianceJob.addComplianceRuleConfiguration(crConfigEntity);
        ExecutionEntity execution = new ExecutionEntity(complianceJob);

        ComplianceIssueEntity issue = new ComplianceIssueEntity(
                crConfigEntity,
                "something is wrong!",
                "wrong-property-value");
        InstanceModel instanceModel = new InstanceModel(null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.fixFirstIssue(execution, instanceModel));
        execution.getComplianceIssueEntities().add(issue);
        IssueFixingReportEntity report = service.fixFirstIssue(execution, instanceModel);
        Assertions.assertNotNull(report);
        Assertions.assertTrue(report.getIsSuccessful());
        Assertions.assertEquals("", report.getDescription());
    }
}
