package org.iac2.service.checking.plugin.implementation.subgraphmatching;

import java.util.ArrayList;
import java.util.Collection;

import org.iac2.common.Plugin;
import org.iac2.common.model.PluginConfigurationEntryDescriptor;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPluginDescriptor;

public class SubgraphMatchingCheckingPluginDescriptor implements ComplianceRuleCheckingPluginDescriptor {
    public static final String IDENTIFIER = "subgraph-matching-checking-plugin";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String getDescription() {
        return "This plugin checks the compliance of the reconstructed instance model to compliance rules of type `subgraph-matching`. " +
                "Such compliance rules are modelled as graphs, and therefore, the compliance checking process uses a subgraph matching algorithm.";
    }

    @Override
    public Collection<PluginConfigurationEntryDescriptor> getConfigurationEntryDescriptors() {
        return new ArrayList<>();
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
