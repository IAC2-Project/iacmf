package org.iac2.service.architecturereconstruction.plugin.implementation.bash;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.iac2.common.Plugin;
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
    public Collection<String> getRequiredConfigurationEntryNames() {
        return List.of(
                CONFIGURATION_ENTRY_SCRIPT,
                CONFIGURATION_ENTRY_OUTPUT_PROPERTY_NAME,
                CONFIGURATION_ENTRY_OUTPUT_PROPERTY_TYPE,
                CONFIGURATION_ENTRY_IGNORE_MISSING_PROPERTIES,
                CONFIGURATION_ENTRY_USERNAME,
                CONFIGURATION_ENTRY_DEFAULT_PRIVATE_KEY,
                CONFIGURATION_ENTRY_PRODUCTION_SYSTEM_ARGUMENTS
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
