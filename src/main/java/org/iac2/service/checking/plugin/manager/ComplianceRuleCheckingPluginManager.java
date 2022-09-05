package org.iac2.service.checking.plugin.manager;

import java.util.Collection;

import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.common.model.compliancerule.ComplianceRule;

public interface ComplianceRuleCheckingPluginManager {

    Collection<ComplianceRuleCheckingPlugin> getPossiblePluginsForComplianceRule(ComplianceRule complianceRule);

    ComplianceRuleCheckingPlugin getPlugin(String identifier) throws PluginNotFoundException;

    Collection<ComplianceRuleCheckingPlugin> getAll();
}
