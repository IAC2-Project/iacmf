package org.iac2.service.checking.service;

import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.common.exception.ComplianceRuleTypeNotSupportedException;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.iac2.service.checking.common.interfaces.RuleValidationResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MockComplianceCheckingPlugin implements ComplianceRuleCheckingPlugin {
    @Override
    public Set<String> requiredConfiguration() {
        return null;
    }

    @Override
    public void setConfiguration(String key, String value) {

    }

    @Override
    public boolean isSuitableForComplianceRule(ComplianceRule complianceRule) {
        return complianceRule.getType().equals("ensure-property-value");
    }

    @Override
    public RuleValidationResult isComplianceRuleValid(ComplianceRule complianceRule) throws ComplianceRuleTypeNotSupportedException {
        return new RuleValidationResult("everything is fine!!") {
            @Override
            public boolean isValid() {
                return true;
            }
        };
    }

    @Override
    public String getIdentifier() {
        return "property-value-checker-plugin";
    }

    @Override
    public Collection<ComplianceIssue> findIssues(InstanceModel instanceModel, ComplianceRule rule) {
        ComplianceIssue issue1 = new ComplianceIssue(
                "your architecture sucks!",
                rule,
                "instance-matches-model",
                new HashMap<>());
        ComplianceIssue issue2 = new ComplianceIssue(
                "your architecture really sucks!",
                rule,
                "user-permissions",
                new HashMap<>());

        return List.of(issue1, issue2);
    }
}
