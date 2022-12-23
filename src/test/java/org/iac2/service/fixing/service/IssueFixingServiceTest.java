package org.iac2.service.fixing.service;

import java.util.Collections;

import org.iac2.common.model.InstanceModel;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.compliancejob.issue.IssueFixingReportEntity;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.plugin.architecturereconstruction.ModelEnhancementStrategyEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.service.fixing.plugin.manager.IssueFixingPluginManager;
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
    IssueFixingPluginManager pluginManager;

    @Autowired
    IssueFixingService service;

    @Test
    void fixFirstIssue() {
        Mockito.when(pluginManager.getPlugin(anyString()))
                .thenReturn(new MockIssueFixingPlugin());

        ComplianceRuleEntity complianceRule = new ComplianceRuleEntity(
                "ensure-property-value",
                "",
                "my useless compliance rule!"
        );
        ProductionSystemEntity productionSystem = new ProductionSystemEntity(
                "my super system",
                "opentoscacontainer",
                "opentosca-container-model-creation-plugin");
        ComplianceJobEntity complianceJob = new ComplianceJobEntity(
                "my super job",
                "property-value-checker",
                "mock",
                productionSystem,
                complianceRule,
                new ModelEnhancementStrategyEntity(Collections.emptyList()),
                Collections.emptyList()
        );
        ExecutionEntity execution = new ExecutionEntity(complianceJob);

        ComplianceIssueEntity issue = new ComplianceIssueEntity(
                execution,
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
