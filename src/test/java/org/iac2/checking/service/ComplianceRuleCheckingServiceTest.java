package org.iac2.checking.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.iac2.checking.common.model.compliancerule.ComplianceRule;
import org.iac2.checking.plugin.manager.ComplianceRuleCheckingPluginManager;
import org.iac2.common.model.SystemModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.entity.compliancerule.ComplianceRuleEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ComplianceRuleCheckingServiceTest {

    @MockBean
    ComplianceRuleCheckingPluginManager pluginManager;

    @Autowired
    ComplianceRuleCheckingService service;

    @Test
    void findIssuesOfSystemModel() {
        ComplianceRuleEntity cr = new ComplianceRuleEntity(
                "subgraphisomorphism",
                "http://nowherer.no",
                "compliacne rule for fooling around");
        Mockito.when(pluginManager.getPossiblePluginsForComplianceRule(any(ComplianceRule.class)))
                .thenReturn(List.of(new MockComplianceCheckingPlugin()));

        Collection<ComplianceIssue> issues = service.findIssuesOfSystemModel(
                cr,
                Collections.emptyList(),
                new SystemModel()
        );

        Assertions.assertNotNull(issues);
        Assertions.assertEquals(2, issues.size());
    }

}