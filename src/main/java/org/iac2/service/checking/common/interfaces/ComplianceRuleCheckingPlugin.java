package org.iac2.service.checking.common.interfaces;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

import org.iac2.common.Plugin;
import org.iac2.common.exception.ConfigurationEntryMissingException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.common.exception.ComplianceRuleMalformattedException;
import org.iac2.service.checking.common.exception.ComplianceRuleTypeNotSupportedException;

public interface ComplianceRuleCheckingPlugin extends Plugin {
    /***
     * Checks the validity of this compliance rule to be used with this plugin (assuming its type is suitable for it).
     * I.e., it checks for structural problems within the rule.
     * @param complianceRule the compliance rule to check for validity.
     * @return true if valid; otherwise, false;
     */
    RuleValidationResult isComplianceRuleValid(ComplianceRule complianceRule)
            throws ComplianceRuleTypeNotSupportedException, URISyntaxException, IOException, InterruptedException;

    // todo do we need additional parameters?
    Collection<ComplianceIssue> findIssues(InstanceModel instanceModel, ComplianceRule rule)
            throws ComplianceRuleTypeNotSupportedException, ComplianceRuleMalformattedException, ConfigurationEntryMissingException;
}
