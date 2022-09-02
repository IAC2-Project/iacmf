package org.iac2.checking.common.interfaces;

import java.util.Collection;

import org.iac2.checking.common.model.compliancerule.ComplianceRule;
import org.iac2.common.model.SystemModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;

public interface ComplianceRuleCheckingPlugin {

    boolean isSuitableForComplianceRule(ComplianceRule complianceRule);
    String getIdentifier();

    // todo do we need additional parameters?
    Collection<ComplianceIssue> findIssues(SystemModel systemModel, ComplianceRule rule);
}
