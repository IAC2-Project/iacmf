package org.iac2.service.checking.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;

public class MockComplianceCheckingPlugin implements ComplianceRuleCheckingPlugin {
    @Override
    public boolean isSuitableForComplianceRule(ComplianceRule complianceRule) {
        return complianceRule.getType().equals("property-value-checker");
    }

    @Override
    public String getIdentifier() {
        return "property-value-checker";
    }

    @Override
    public Collection<ComplianceIssue> findIssues(InstanceModel systemModel, ComplianceRule rule) {
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
