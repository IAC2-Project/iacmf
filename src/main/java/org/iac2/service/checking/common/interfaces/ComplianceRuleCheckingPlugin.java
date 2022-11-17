package org.iac2.service.checking.common.interfaces;

import java.util.Collection;
import java.util.Set;

import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;

public interface ComplianceRuleCheckingPlugin {

    /***
     * Reports the required set of configuration entry names needed for the operation of the plugin.
     * @return the set of configuration entry names needed for the operation of the plugin.
     */
    Set<String> requiredConfiguration();

    void setConfiguration(String key, String value);

    boolean isSuitableForComplianceRule(ComplianceRule complianceRule);

    String getIdentifier();

    // todo do we need additional parameters?
    Collection<ComplianceIssue> findIssues(InstanceModel instanceModel, ComplianceRule rule);
}
