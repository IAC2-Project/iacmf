package org.iac2.service.fixing.plugin.manager.implementation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.common.exception.PluginType;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.iac2.service.fixing.plugin.implementaiton.opentoscacontainer.OpenToscaContainerIssueFixingPlugin;
import org.iac2.service.fixing.plugin.manager.IssueFixingPluginManager;

public class SimpleIssueFixingPluginManager implements IssueFixingPluginManager {
    private static SimpleIssueFixingPluginManager instance;
    private final Map<String, IssueFixingPlugin> allPlugins;

    private SimpleIssueFixingPluginManager() {
        this.allPlugins = new HashMap<>();
        OpenToscaContainerIssueFixingPlugin otcPlugin = new OpenToscaContainerIssueFixingPlugin();
        this.allPlugins.put(otcPlugin.getIdentifier(), otcPlugin);
    }

    public static SimpleIssueFixingPluginManager getInstance() {
        if (instance == null) {
            instance = new SimpleIssueFixingPluginManager();
        }

        return instance;
    }

    @Override
    public Collection<IssueFixingPlugin> getSuitablePlugins(ComplianceIssue complianceIssue, ProductionSystem productionSystem) {
        return allPlugins
                .values()
                .stream()
                .filter(p -> p.isSuitableForIssue(complianceIssue) && p.isSuitableForProductionSystem(productionSystem))
                .toList();
    }

    @Override
    public IssueFixingPlugin getPlugin(String identifier) {
        IssueFixingPlugin plugin = this.allPlugins.get(identifier);

        if (plugin == null) {
            throw new PluginNotFoundException(identifier, PluginType.ISSUE_CHECKING);
        }

        return plugin;
    }

    @Override
    public Collection<IssueFixingPlugin> getAll() {
        return this.allPlugins.values();
    }
}
