package org.iac2.service.checking.common.interfaces;

import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.common.exception.ComplianceRuleMalformattedException;
import org.iac2.service.checking.common.exception.ComplianceRuleTypeNotSupportedException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Set;

public interface ComplianceRuleCheckingPlugin {

    /***
     * Reports the required set of configuration entry names needed for the operation of the plugin.
     * @return the set of configuration entry names needed for the operation of the plugin.
     */
    Set<String> requiredConfiguration();

    void setConfiguration(String key, String value);

    /***
     * Checks whether the type of the rule suitable for this plugin
     * @param complianceRule the compliance rule to check for suitability
     * @return true if suitable; otherwise, false.
     */
    boolean isSuitableForComplianceRule(ComplianceRule complianceRule);

    /***
     * Checks the validity of this compliance rule to be used with this plugin (assuming its type is suitable for it).
     * I.e., it checks for structural problems within the rule.
     * @param complianceRule the compliance rule to check for validity.
     * @return true if valid; otherwise, false;
     */
    RuleValidationResult isComplianceRuleValid(ComplianceRule complianceRule) throws ComplianceRuleTypeNotSupportedException, URISyntaxException, IOException, InterruptedException;


    String getIdentifier();

    // todo do we need additional parameters?
    Collection<ComplianceIssue> findIssues(InstanceModel instanceModel, ComplianceRule rule)
            throws ComplianceRuleTypeNotSupportedException, ComplianceRuleMalformattedException;
}
