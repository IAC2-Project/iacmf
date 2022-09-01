package org.iac2.checking.plugin.manager;

import java.util.Collection;

import org.iac2.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.iac2.checking.common.model.compliancerule.ComplianceRule;

public interface ComplianceRuleCheckingPluginManager {

    Collection<ComplianceRuleCheckingPlugin> getPossiblePluginsForComplianceRule(ComplianceRule complianceRule);

    ComplianceRuleCheckingPlugin getPlugin(String identifier);

    Collection<ComplianceRuleCheckingPlugin> getAll();
}
