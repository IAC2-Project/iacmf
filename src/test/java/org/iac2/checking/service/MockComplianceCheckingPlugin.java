package org.iac2.checking.service;

import java.util.Collection;
import java.util.List;

import org.iac2.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.iac2.checking.common.model.compliancerule.ComplianceRule;
import org.iac2.common.model.SystemModel;
import org.iac2.common.model.compliancejob.issue.ArchitecturalComplianceIssue;
import org.iac2.common.model.compliancejob.issue.ArchitecturalIssueType;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;

public class MockComplianceCheckingPlugin implements ComplianceRuleCheckingPlugin {
    @Override
    public boolean isSuitableForComplianceRule(ComplianceRule complianceRule) {
        return complianceRule.getType().equals("subgraphisomorphism");
    }

    @Override
    public String getIdentifier() {
        return "mock-subgraphisomorphism-plugin";
    }

    @Override
    public Collection<ComplianceIssue> findIssues(SystemModel systemModel, ComplianceRule rule) {
        ComplianceIssue issue1 = new ArchitecturalComplianceIssue(
                "your architecture sucks!",
                "application-1/user-name",
                ArchitecturalIssueType.MISSING_PROPERTY);
        ComplianceIssue issue2 = new ArchitecturalComplianceIssue(
                "your architecture really sucks!",
                "vm2",
                ArchitecturalIssueType.EXTRA_NODE);

        return List.of(issue1, issue2);
    }
}
