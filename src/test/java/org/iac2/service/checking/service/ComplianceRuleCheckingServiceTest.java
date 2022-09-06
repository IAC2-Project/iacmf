package org.iac2.service.checking.service;

import java.util.Collection;
import java.util.Collections;

import org.iac2.common.model.InstanceModel;
import org.iac2.entity.architecturereconstruction.ModelEnhancementStrategyEntity;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.compliancejob.issue.ComplianceIssueEntity;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.service.checking.plugin.manager.ComplianceRuleCheckingPluginManager;
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
class ComplianceRuleCheckingServiceTest {

    @MockBean
    ComplianceRuleCheckingPluginManager pluginManager;

    @Autowired
    ComplianceRuleCheckingService service;

    @Test
    void findIssuesOfSystemModel() {
        Mockito.when(pluginManager.getPlugin(anyString()))
                .thenReturn(new MockComplianceCheckingPlugin());

        ComplianceRuleEntity cr = new ComplianceRuleEntity(
                "ensure-property-value",
                "http://nowherer.no",
                "compliacne rule for fooling around");
        ProductionSystemEntity productionSystem = new ProductionSystemEntity(
                "some system",
                "opentoscacontainer",
                "opentosca-container-model-creation-plugin");
        ModelEnhancementStrategyEntity modelEnhancementStrategy = new ModelEnhancementStrategyEntity(Collections.emptyList());
        ComplianceJobEntity complianceJob = new ComplianceJobEntity(
                "a fine job",
                "property-value-checker-plugin",
                "opentosca-container-issue-fixing-plugin",
                productionSystem,
                cr,
                modelEnhancementStrategy,
                Collections.emptyList()
        );
        ExecutionEntity execution = new ExecutionEntity(complianceJob);

        Collection<ComplianceIssueEntity> issues = service.findIssuesOfSystemModel(
                execution,
                new InstanceModel(null)
        );

        Assertions.assertNotNull(issues);
        Assertions.assertEquals(2, issues.size());
    }
}