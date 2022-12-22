package org.iac2.service.checking.plugin.manager;

import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;

import java.util.Collection;

public interface ComplianceRuleCheckingPluginManager {

    Collection<ComplianceRuleCheckingPlugin> getPossiblePluginsForComplianceRule(ComplianceRule complianceRule);

    ComplianceRuleCheckingPlugin getPlugin(String identifier) throws PluginNotFoundException;

    Collection<ComplianceRuleCheckingPlugin> getAll();
}
