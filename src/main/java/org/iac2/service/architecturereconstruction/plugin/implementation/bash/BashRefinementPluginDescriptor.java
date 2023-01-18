package org.iac2.service.architecturereconstruction.plugin.implementation.bash;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.iac2.common.Plugin;
import org.iac2.common.model.PluginConfigurationEntryDescriptor;
import org.iac2.common.model.PluginConfigurationEntryType;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelRefinementPluginDescriptor;

public class BashRefinementPluginDescriptor implements ModelRefinementPluginDescriptor {
    public static final String IDENTIFIER = "bash-refinement-plugin";

    public static final String CONFIGURATION_ENTRY_SCRIPT = "script";
    public static final String CONFIGURATION_ENTRY_OUTPUT_PROPERTY_NAME = "output_property_name";
    public static final String CONFIGURATION_ENTRY_OUTPUT_PROPERTY_TYPE = "output_property_type";
    public static final String CONFIGURATION_ENTRY_IGNORE_MISSING_PROPERTIES = "ignore-missing-properties";
    public static final String CONFIGURATION_ENTRY_PRODUCTION_SYSTEM_ARGUMENTS = "production-system-arguments";
    public static final String CONFIGURATION_ENTRY_USERNAME = "username";
    public static final String CONFIGURATION_ENTRY_DEFAULT_PRIVATE_KEY = "default-private-key-path";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String getDescription() {
        return "This plugin is capable of running a user-defined bash script over ssh on an ubuntu-based (virtual-)machine" +
                " in order to retrieve information from it, and update the instance model with this information.";
    }

    @Override
    public Collection<PluginConfigurationEntryDescriptor> getConfigurationEntryDescriptors() {
        return List.of(
                new PluginConfigurationEntryDescriptor(
                        CONFIGURATION_ENTRY_SCRIPT,
                        PluginConfigurationEntryType.BASH_COMMAND,
                        true,
                        "the bash script to be executed via ssh. A value is expected to be returned from the execution of the script."
                ),
                new PluginConfigurationEntryDescriptor(
                        CONFIGURATION_ENTRY_USERNAME,
                        PluginConfigurationEntryType.STRING,
                        true,
                        "the username to be used when connecting to the ubuntu (virtual-)machine via ssh."
                ),
                new PluginConfigurationEntryDescriptor(
                        CONFIGURATION_ENTRY_OUTPUT_PROPERTY_NAME,
                        PluginConfigurationEntryType.STRING,
                        true,
                        "The name of the property that will be added to the affected `Compute` components of the " +
                                "instance model in order to hold the values that are retrieved from the ubuntu (virtual-)machines " +
                                "using the bash script. If this property already exists in the components, its value is updated with " +
                                "the retrieved values."
                ),

                new PluginConfigurationEntryDescriptor(
                        CONFIGURATION_ENTRY_OUTPUT_PROPERTY_TYPE,
                        PluginConfigurationEntryType.STRING,
                        true,
                        "the type of the property that will be added to the affected `Compute` components of the " +
                                "instance model (see `output_property_name` above). The possible values for this configuration entry are: " +
                                "1. `STRING` . `INT` 3. `DECIMAL` 4. `STRING_LIST` 5. `BOOLEAN`"
                ),

                new PluginConfigurationEntryDescriptor(
                        CONFIGURATION_ENTRY_IGNORE_MISSING_PROPERTIES,
                        PluginConfigurationEntryType.BOOLEAN,
                        true,
                        "Indicates whether the plugin will ignore the `Compute` nodes that represent ubuntu (virtual-)machines but" +
                                " do not provide enough information to facilitate communicating with them (e.g., missing `public_address` property)." +
                                " If the value is `false`, the plugin will throw an exception if such a component is detected in the input instance model."
                ),

                new PluginConfigurationEntryDescriptor(
                        CONFIGURATION_ENTRY_DEFAULT_PRIVATE_KEY,
                        PluginConfigurationEntryType.FILE_PATH,
                        false,
                        "The path (on the iacmf server) to the private key that allows to connect to the ubuntu (virtual-)machine. " +
                                "This entry will be used iff the instance model node that has the issue does not define a property `private_key_path`. " +
                                "At least one of these two values must be set."
                ),

                new PluginConfigurationEntryDescriptor(
                        CONFIGURATION_ENTRY_PRODUCTION_SYSTEM_ARGUMENTS,
                        PluginConfigurationEntryType.STRING,
                        false,
                        "A comma-separated list of production system parameter names. If this value is set, the plugin will retrieve the" +
                                " referenced attributes and pass their values to the bash script as command-line arguments in the same order " +
                                "specified in this list."
                )
        );
    }

    @Override
    public Plugin createPlugin() {
        return new BashRefinementPlugin(this);
    }

    @Override
    public Collection<String> getRequiredProductionSystemPropertyNames() {
        return new ArrayList<>();
    }
}
