package org.iac2.service.fixing.plugin.factory.implementation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.iac2.common.PluginDescriptor;
import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.common.model.PluginType;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.iac2.service.fixing.common.interfaces.IssueFixingPluginDescriptor;
import org.iac2.service.fixing.plugin.factory.IssueFixingPluginFactory;
import org.iac2.service.fixing.plugin.implementaiton.bash.BashFixingPluginDescriptor;
import org.iac2.service.fixing.plugin.implementaiton.docker.DockerContainerIssueFixingPluginDescriptor;
import org.iac2.service.fixing.plugin.implementaiton.mysql.RemoveDBUsersFixingPluginDescriptor;

public class SimpleIssueFixingPluginFactory implements IssueFixingPluginFactory {
    private static SimpleIssueFixingPluginFactory instance;
    private final Map<String, IssueFixingPluginDescriptor> allPlugins;

    private SimpleIssueFixingPluginFactory() {
        this.allPlugins = new HashMap<>();
        DockerContainerIssueFixingPluginDescriptor dockerPlugin = new DockerContainerIssueFixingPluginDescriptor();
        RemoveDBUsersFixingPluginDescriptor mysql = new RemoveDBUsersFixingPluginDescriptor();
        BashFixingPluginDescriptor bash = new BashFixingPluginDescriptor();
        this.allPlugins.put(dockerPlugin.getIdentifier(), dockerPlugin);
        this.allPlugins.put(mysql.getIdentifier(), mysql);
        this.allPlugins.put(bash.getIdentifier(), bash);
    }

    public static SimpleIssueFixingPluginFactory getInstance() {
        if (instance == null) {
            instance = new SimpleIssueFixingPluginFactory();
        }

        return instance;
    }

    @Override
    public Collection<String> getSuitablePluginIdentifiers(String complianceIssue, ProductionSystem productionSystem) {
        return allPlugins
                .values()
                .stream()
                .filter(p -> p.isIssueTypeSupported(complianceIssue) &&
                        p.isIaCTechnologySupported(productionSystem.getIacTechnologyName()))
                .map(PluginDescriptor::getIdentifier)
                .toList();
    }

    @Override
    public IssueFixingPlugin createPlugin(String identifier) {
        IssueFixingPluginDescriptor plugin = this.allPlugins.get(identifier);

        if (plugin == null) {
            throw new PluginNotFoundException(identifier, PluginType.ISSUE_CHECKING);
        }

        return (IssueFixingPlugin) plugin.createPlugin();
    }

    @Override
    public Collection<String> getAllPluginIdentifiers() {
        return this.allPlugins.values().stream().map(PluginDescriptor::getIdentifier).toList();
    }

    @Override
    public boolean pluginExists(String identifier) {
        return this.allPlugins.containsKey(identifier);
    }

    @Override
    public PluginDescriptor describePlugin(String identifier) {
        return allPlugins.get(identifier);
    }
}
