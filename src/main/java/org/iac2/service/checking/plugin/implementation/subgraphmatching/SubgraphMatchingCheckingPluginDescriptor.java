package org.iac2.service.checking.plugin.implementation.subgraphmatching;

import java.util.Collection;
import java.util.HashSet;

import org.iac2.common.Plugin;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPluginDescriptor;

public class SubgraphMatchingCheckingPluginDescriptor implements ComplianceRuleCheckingPluginDescriptor {
    public static final String IDENTIFIER = "subgraph-matching-checking-plugin";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Collection<String> getRequiredConfigurationEntryNames() {
        return new HashSet<>();
    }

    @Override
    public Plugin createPlugin() {
        return new SubgraphMatchingCheckingPlugin(this);
    }

    @Override
    public boolean isSuitableForComplianceRule(ComplianceRule complianceRule) {
        return complianceRule.getType().equals("subgraph-matching");
    }
}
