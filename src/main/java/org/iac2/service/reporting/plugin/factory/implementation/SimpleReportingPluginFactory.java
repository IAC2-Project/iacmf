package org.iac2.service.reporting.plugin.factory.implementation;

import org.iac2.common.Plugin;
import org.iac2.common.PluginDescriptor;
import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.common.model.PluginType;
import org.iac2.service.fixing.common.interfaces.IssueFixingPluginDescriptor;
import org.iac2.service.fixing.plugin.factory.implementation.SimpleIssueFixingPluginFactory;
import org.iac2.service.reporting.common.interfaces.ReportingPlugin;
import org.iac2.service.reporting.common.interfaces.ReportingPluginDescriptor;
import org.iac2.service.reporting.plugin.factory.ReportingPluginFactory;
import org.iac2.service.reporting.plugin.implementation.email.EmailReportingPluginDescriptor;
import org.iac2.service.reporting.plugin.implementation.noops.NoOperationPluginDescriptor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimpleReportingPluginFactory implements ReportingPluginFactory {

    private static SimpleReportingPluginFactory instance;
    private final Map<String, ReportingPluginDescriptor> allPlugins;

    private SimpleReportingPluginFactory(){
        this.allPlugins = new HashMap<>();
        NoOperationPluginDescriptor noops = new NoOperationPluginDescriptor();
        EmailReportingPluginDescriptor email = new EmailReportingPluginDescriptor();
        this.allPlugins.put(noops.getIdentifier(), noops);
        this.allPlugins.put(email.getIdentifier(), email);
    }

    public static SimpleReportingPluginFactory getInstance() {
        if(instance == null) {
            instance = new SimpleReportingPluginFactory();
        }

        return instance;
    }

    @Override
    public Plugin createPlugin(String identifier) throws PluginNotFoundException {
        ReportingPluginDescriptor descriptor = this.allPlugins.get(identifier);

        if (descriptor == null) {
            throw new PluginNotFoundException(identifier, PluginType.REPORTING);
        }

        return descriptor.createPlugin();
    }

    @Override
    public Collection<String> getAllPluginIdentifiers() {
        return this.allPlugins.keySet();
    }

    @Override
    public PluginDescriptor describePlugin(String identifier) {
        return this.allPlugins.get(identifier);
    }

    @Override
    public boolean pluginExists(String identifier) {
        return this.allPlugins.containsKey(identifier);
    }
}
