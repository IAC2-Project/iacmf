package org.iac2.service.checking.common.interfaces;

import java.util.Collection;

import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;

public interface ComplianceRuleCheckingPlugin {

    boolean isSuitableForComplianceRule(ComplianceRule complianceRule);
    String getIdentifier();

    // todo do we need additional parameters?
    Collection<ComplianceIssue> findIssues(InstanceModel systemModel, ComplianceRule rule);
}
