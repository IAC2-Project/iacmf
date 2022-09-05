package org.iac2.checking.plugin.manager.implementation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.iac2.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.iac2.checking.common.model.compliancerule.ComplianceRule;
import org.iac2.checking.plugin.implementation.SubgraphIsomorphismCheckerPlugin;
import org.iac2.checking.plugin.manager.ComplianceRuleCheckingPluginManager;

/**
 * Plugin manager with hard-coded plugin classes.
 * This class is provided as a bean (singleton). Check the application class.
 */
public class SimpleCRCheckingManager implements ComplianceRuleCheckingPluginManager {
    private static SimpleCRCheckingManager instance;
    private final Map<String, ComplianceRuleCheckingPlugin> allPlugins;

    private SimpleCRCheckingManager() {
        SubgraphIsomorphismCheckerPlugin isomorphismCheckerPlugin = new SubgraphIsomorphismCheckerPlugin();
        allPlugins = new HashMap<>();
        allPlugins.put(isomorphismCheckerPlugin.getIdentifier(), isomorphismCheckerPlugin);
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
        return allPlugins.get(identifier);
    }

    @Override
    public Collection<ComplianceRuleCheckingPlugin> getAll() {
        return allPlugins.values();
    }
}
