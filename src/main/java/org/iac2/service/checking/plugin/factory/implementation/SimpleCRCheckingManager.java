package org.iac2.service.checking.plugin.factory.implementation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.iac2.common.PluginDescriptor;
import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.common.model.PluginType;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPluginDescriptor;
import org.iac2.service.checking.plugin.factory.ComplianceRuleCheckingPluginFactory;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.SubgraphMatchingCheckingPluginDescriptor;

/**
 * Plugin manager with hard-coded plugin classes.
 * This class is provided as a bean (singleton). Check the application class.
 */
public class SimpleCRCheckingManager implements ComplianceRuleCheckingPluginFactory {
    private static SimpleCRCheckingManager instance;
    private final Map<String, ComplianceRuleCheckingPluginDescriptor> allPlugins;

    private SimpleCRCheckingManager() {
        SubgraphMatchingCheckingPluginDescriptor plugind = new SubgraphMatchingCheckingPluginDescriptor();
        allPlugins = new HashMap<>();
        allPlugins.put(plugind.getIdentifier(), plugind);
    }

    public static SimpleCRCheckingManager getInstance() {
        if (instance == null) {
            instance = new SimpleCRCheckingManager();
        }

        return instance;
    }

    @Override
    public Collection<String> getPossiblePluginIdentifiersForComplianceRule(ComplianceRule complianceRule) {
        return allPlugins.values()
                .stream()
                .filter(p -> p.isSuitableForComplianceRule(complianceRule))
                .map(PluginDescriptor::getIdentifier)
                .toList();
    }

    @Override
    public ComplianceRuleCheckingPlugin createPlugin(String identifier) {
        ComplianceRuleCheckingPluginDescriptor pluginD = allPlugins.get(identifier);

        if (pluginD == null) {
            throw new PluginNotFoundException(identifier, PluginType.ISSUE_CHECKING);
        }

        return (ComplianceRuleCheckingPlugin) pluginD.createPlugin();
    }

    @Override
    public Collection<String> getAllPluginIdentifiers() {
        return allPlugins.values().stream().map(PluginDescriptor::getIdentifier).collect(Collectors.toList());
    }

    @Override
    public boolean pluginExists(String identifier) {
        return allPlugins.containsKey(identifier);
    }

    @Override
    public PluginDescriptor describePlugin(String identifier) {
        return allPlugins.get(identifier);
    }
}
