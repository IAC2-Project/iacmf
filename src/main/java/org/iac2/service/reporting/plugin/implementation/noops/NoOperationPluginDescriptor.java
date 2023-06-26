package org.iac2.service.reporting.plugin.implementation.noops;

import org.iac2.common.Plugin;
import org.iac2.common.model.PluginConfigurationEntryDescriptor;
import org.iac2.service.reporting.common.interfaces.ReportingPluginDescriptor;

import java.util.ArrayList;
import java.util.Collection;

public class NoOperationPluginDescriptor implements ReportingPluginDescriptor {
    @Override
    public String getIdentifier() {
        return "no-ops-reporting-plugin";
    }

    @Override
    public String getDescription() {
        return "This plugin does not perform any reporting!";
    }

    @Override
    public Collection<PluginConfigurationEntryDescriptor> getConfigurationEntryDescriptors() {
        return new ArrayList<>();
    }

    @Override
    public Plugin createPlugin() {
        return new NoOperationReportingPlugin(this);
    }
}
