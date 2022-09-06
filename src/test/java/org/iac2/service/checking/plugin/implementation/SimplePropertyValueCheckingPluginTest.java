package org.iac2.service.checking.plugin.implementation;

import java.io.IOException;
import java.util.Collection;

import io.github.edmm.model.DeploymentModel;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.model.compliancerule.parameter.StringComplianceRuleParameter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class SimplePropertyValueCheckingPluginTest {
    SimplePropertyValueCheckingPlugin plugin = new SimplePropertyValueCheckingPlugin();

    @Test
    void testSuitabilityCheck() {
        ComplianceRule rule = new ComplianceRule("abc", "");
        Assertions.assertFalse(plugin.isSuitableForComplianceRule(rule));
        rule.setType("ensure-property-value");
        Assertions.assertTrue(plugin.isSuitableForComplianceRule(rule));
    }

    @Test
    void testPropertyAtInstanceModelLevel() {
        ComplianceRule rule = new ComplianceRule("ensure-property-value", "");
        InstanceModel instanceModel = new InstanceModel(null);
        Assertions.assertThrows (IllegalArgumentException.class, () -> plugin.findIssues(instanceModel, rule));
        rule.addStringParameter(SimplePropertyValueCheckingPlugin.ENTITY_ID_KEY, "abc");
        Assertions.assertThrows (IllegalArgumentException.class, () -> plugin.findIssues(instanceModel, rule));
        rule.addStringParameter(SimplePropertyValueCheckingPlugin.PROPERTY_NAME_KEY, "abc");
        Assertions.assertThrows (IllegalArgumentException.class, () -> plugin.findIssues(instanceModel, rule));
        rule.addStringParameter(SimplePropertyValueCheckingPlugin.PROPERTY_VALUE_KEY, "abc");
        // test missing entity
        Collection<ComplianceIssue> issues = plugin.findIssues(instanceModel, rule);
        Assertions.assertNotNull(issues);
        Assertions.assertEquals(1, issues.size());
        Assertions.assertEquals(SimplePropertyValueCheckingPlugin.MISSING_ENTITY_ISSUE_TYPE,
                issues.stream().findFirst().get().getType());

        // test missing property
        rule.getParameterAssignments()
                .stream()
                .filter(a -> a.getName().equals(SimplePropertyValueCheckingPlugin.ENTITY_ID_KEY))
                .map(a -> (StringComplianceRuleParameter) a)
                .findFirst()
                .get()
                .setValue(SimplePropertyValueCheckingPlugin.ENTITY_ID_FOR_INSTANCE_MODEL);
        issues = plugin.findIssues(instanceModel, rule);
        Assertions.assertEquals(SimplePropertyValueCheckingPlugin.MISSING_PROPERTY_ISSUE_TYPE,
                issues.stream().findFirst().get().getType());

        // test missing property
        instanceModel.getProperties().put("ccc", "kkk");
        issues = plugin.findIssues(instanceModel, rule);
        Assertions.assertEquals(SimplePropertyValueCheckingPlugin.MISSING_PROPERTY_ISSUE_TYPE,
                issues.stream().findFirst().get().getType());

        // test wrong property value
        instanceModel.getProperties().put("abc", "kkk");
        issues = plugin.findIssues(instanceModel, rule);
        Assertions.assertEquals(SimplePropertyValueCheckingPlugin.WRONG_PROPERTY_VALUE_ISSUE_TYPE,
                issues.stream().findFirst().get().getType());

        // test no issues
        instanceModel.getProperties().put("abc", "abc");
        issues = plugin.findIssues(instanceModel, rule);
        Assertions.assertEquals(0, issues.size());
    }

    @Test
    void testPropertyInComponent() throws IOException {
        ClassPathResource resource = new ClassPathResource("edmm/three-components-hosted-on.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        InstanceModel instanceModel = new InstanceModel(model);

        ComplianceRule rule = new ComplianceRule("ensure-property-value", "");

        rule.addStringParameter(SimplePropertyValueCheckingPlugin.ENTITY_ID_KEY, "ubuntu");
        rule.addStringParameter(SimplePropertyValueCheckingPlugin.PROPERTY_NAME_KEY, "oss_family");
        rule.addStringParameter(SimplePropertyValueCheckingPlugin.PROPERTY_VALUE_KEY, "linuxx");

        // test missing property
        Collection<ComplianceIssue> issues = plugin.findIssues(instanceModel, rule);
        Assertions.assertNotNull(issues);
        Assertions.assertEquals(1, issues.size());
        Assertions.assertEquals(SimplePropertyValueCheckingPlugin.MISSING_PROPERTY_ISSUE_TYPE,
                issues.stream().findFirst().get().getType());

        // test wrong value
        rule.getParameterAssignments()
                .stream()
                .filter(a -> a.getName().equals(SimplePropertyValueCheckingPlugin.PROPERTY_NAME_KEY))
                .map(a -> (StringComplianceRuleParameter) a)
                .findFirst()
                .get()
                .setValue("os_family");
        issues = plugin.findIssues(instanceModel, rule);
        Assertions.assertNotNull(issues);
        Assertions.assertEquals(1, issues.size());
        Assertions.assertEquals(SimplePropertyValueCheckingPlugin.WRONG_PROPERTY_VALUE_ISSUE_TYPE,
                issues.stream().findFirst().get().getType());

        // test no issues
        rule.getParameterAssignments()
                .stream()
                .filter(a -> a.getName().equals(SimplePropertyValueCheckingPlugin.PROPERTY_VALUE_KEY))
                .map(a -> (StringComplianceRuleParameter) a)
                .findFirst()
                .get()
                .setValue("linux");
        issues = plugin.findIssues(instanceModel, rule);
        Assertions.assertNotNull(issues);
        Assertions.assertEquals(0, issues.size());

    }
}