package org.iac2.service.checking.plugin.implementation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.common.exception.ComplianceRuleMalformattedException;
import org.iac2.service.checking.common.exception.ComplianceRuleTypeNotSupportedException;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;

public class SimplePropertyValueCheckingPlugin implements ComplianceRuleCheckingPlugin {

    @Override
    public boolean isSuitableForComplianceRule(ComplianceRule complianceRule) {
        return complianceRule.getType().toLowerCase().equals(getIdentifier());
    }

    @Override
    public String getIdentifier() {
        return "property-value-checker";
    }

    @Override
    public Collection<ComplianceIssue> findIssues(InstanceModel instanceModel, ComplianceRule rule) {
        // cheating: instead of retrieving the compliance rule model from some external source,
        // we assume it is embedded in the location variable
        final String ruleText = rule.getLocation();
        String[] parts = ruleText.split("=");

        if (!isSuitableForComplianceRule(rule)) {
            throw new ComplianceRuleTypeNotSupportedException(rule.getType());
        }

        if (parts.length != 2) {
            throw new ComplianceRuleMalformattedException(
                    "Expected format: key=value , but got " + rule.getLocation() + " instead.");
        }

        final String key = parts[0];
        final String value = parts[1];

        if (!instanceModel.getProperties().containsKey(key)) {
            Map<String,String> issueProperties = new HashMap<>();
            issueProperties.put("property-name", key);

            return List.of(new ComplianceIssue(
                    "A property is missing from the instance model",
                    rule,
                    "missing-property",
                    issueProperties
            ));
        }

        if (!instanceModel.getProperties().get(key).equals(value)) {
            Map<String,String> issueProperties = new HashMap<>();
            issueProperties.put("property-name", key);
            issueProperties.put("expected-value", value);

            return List.of(new ComplianceIssue(
                    "A property value has an unexpected value.",
                    rule,
                    "wrong-property-value",
                    issueProperties
            ));
        }

        return Collections.emptyList();
    }
}
