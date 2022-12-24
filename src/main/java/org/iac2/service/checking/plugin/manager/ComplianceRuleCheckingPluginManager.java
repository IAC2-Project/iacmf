package org.iac2.service.checking.plugin.manager;

import java.util.Collection;

import org.iac2.common.PluginManager;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;

public interface ComplianceRuleCheckingPluginManager extends PluginManager {

    Collection<ComplianceRuleCheckingPlugin> getPossiblePluginsForComplianceRule(ComplianceRule complianceRule);
}
