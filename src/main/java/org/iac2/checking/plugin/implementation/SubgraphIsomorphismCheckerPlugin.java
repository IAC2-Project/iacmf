package org.iac2.checking.plugin.implementation;

import java.util.Collection;

import org.iac2.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.iac2.checking.common.model.compliancerule.ComplianceRule;
import org.iac2.common.model.SystemModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;

public class SubgraphIsomorphismCheckerPlugin implements ComplianceRuleCheckingPlugin {

    @Override
    public boolean isSuitableForComplianceRule(ComplianceRule complianceRule) {
        return complianceRule.getType().toLowerCase().equals(getIdentifier());
    }

    @Override
    public String getIdentifier() {
        return "subgraphisomorphism";
    }

    @Override
    public Collection<ComplianceIssue> findIssues(SystemModel systemModel, ComplianceRule rule) {
        return null;
    }
}
