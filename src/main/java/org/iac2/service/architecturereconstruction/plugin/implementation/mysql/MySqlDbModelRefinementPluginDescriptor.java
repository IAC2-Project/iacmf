package org.iac2.service.architecturereconstruction.plugin.implementation.mysql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.iac2.common.Plugin;
import org.iac2.common.model.PluginConfigurationEntryDescriptor;
import org.iac2.common.model.PluginConfigurationEntryType;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelRefinementPluginDescriptor;

public class MySqlDbModelRefinementPluginDescriptor implements ModelRefinementPluginDescriptor {
    public static final String CONFIG_ENTRY_IGNORE_MISSING_PROPERTIES = "ignoreMissingProperties";

    @Override
    public Collection<String> getRequiredProductionSystemPropertyNames() {
        return new ArrayList<>();
    }

    @Override
    public String getIdentifier() {
        return "mysql-db-model-refinement-plugin";
    }

    @Override
    public String getDescription() {
        return "This plugin allows refining the instance model with information about all the users that have permissions on the " +
                "MySQL database components present in the instance model. This information will be stored as a comma-separated list of usernames " +
                "assigned to a property called `users`.";
    }

    @Override
    public Collection<PluginConfigurationEntryDescriptor> getConfigurationEntryDescriptors() {
        return List.of(
                new PluginConfigurationEntryDescriptor(
                        CONFIG_ENTRY_IGNORE_MISSING_PROPERTIES,
                        PluginConfigurationEntryType.BOOLEAN,
                        true,
                        "Determines whether to ignore MySQL database instance model components" +
                                "that do not have the required properties or to throw an Exception if this situation occurs.")
        );
    }

    @Override
    public Plugin createPlugin() {
        return new MySqlDbModelRefinementPlugin(this);
    }
}