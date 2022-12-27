package org.iac2.service.checking.plugin.factory;

import java.util.Collection;

import org.iac2.common.PluginFactory;
import org.iac2.common.model.compliancerule.ComplianceRule;

public interface ComplianceRuleCheckingPluginFactory extends PluginFactory {

    Collection<String> getPossiblePluginIdentifiersForComplianceRule(ComplianceRule complianceRule);
}
