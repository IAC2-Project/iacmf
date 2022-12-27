package org.iac2.service.architecturereconstruction.plugin.implementation.mysql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.iac2.common.Plugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelRefinementPluginDescriptor;

public class MySqlDbModelRefinementPluginDescriptor implements ModelRefinementPluginDescriptor {
    @Override
    public Collection<String> getRequiredProductionSystemPropertyNames() {
        return new ArrayList<>();
    }

    @Override
    public String getIdentifier() {
        return "mysql-db-model-refinement-plugin";
    }

    @Override
    public Collection<String> getRequiredConfigurationEntryNames() {
        return List.of(MySqlDbModelRefinementPlugin.CONFIG_ENTRY_IGNORE_MISSING_PROPERTIES);
    }

    @Override
    public Plugin createPlugin() {
        return new MySqlDbModelRefinementPlugin(this);
    }
}