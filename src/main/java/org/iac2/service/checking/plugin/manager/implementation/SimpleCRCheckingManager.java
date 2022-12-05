package org.iac2.service.checking.plugin.manager.implementation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.common.exception.PluginType;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.SubgraphMatchingCheckingPlugin;
import org.iac2.service.checking.plugin.manager.ComplianceRuleCheckingPluginManager;

/**
 * Plugin manager with hard-coded plugin classes.
 * This class is provided as a bean (singleton). Check the application class.
 */
public class SimpleCRCheckingManager implements ComplianceRuleCheckingPluginManager {
    private static SimpleCRCheckingManager instance;
    private final Map<String, ComplianceRuleCheckingPlugin> allPlugins;

    private SimpleCRCheckingManager() {
        SubgraphMatchingCheckingPlugin plugin = new SubgraphMatchingCheckingPlugin();
        allPlugins = new HashMap<>();
        allPlugins.put(plugin.getIdentifier(), plugin);

    }

    public static SimpleCRCheckingManager getInstance() {
        if (instance == null) {
            instance = new SimpleCRCheckingManager();
        }

        return instance;
    }

    @Override
    public Collection<ComplianceRuleCheckingPlugin> getPossiblePluginsForComplianceRule(ComplianceRule complianceRule) {
        return allPlugins.values()
                .stream()
                .filter(p -> p.isSuitableForComplianceRule(complianceRule))
                .toList();
    }

    @Override
    public ComplianceRuleCheckingPlugin getPlugin(String identifier) {
        ComplianceRuleCheckingPlugin plugin = allPlugins.get(identifier);

        if (plugin == null) {
            throw new PluginNotFoundException(identifier, PluginType.ISSUE_CHECKING);
        }

        return plugin;
    }

    @Override
    public Collection<ComplianceRuleCheckingPlugin> getAll() {
        return allPlugins.values();
    }
}
