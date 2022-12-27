package org.iac2.service.checking.common.interfaces;

import org.iac2.common.PluginDescriptor;
import org.iac2.common.model.compliancerule.ComplianceRule;

public interface ComplianceRuleCheckingPluginDescriptor extends PluginDescriptor {
    /***
     * Checks whether the type of the rule suitable for this plugin
     * @param complianceRule the compliance rule to check for suitability
     * @return true if suitable; otherwise, false.
     */
    boolean isSuitableForComplianceRule(ComplianceRule complianceRule);
}
