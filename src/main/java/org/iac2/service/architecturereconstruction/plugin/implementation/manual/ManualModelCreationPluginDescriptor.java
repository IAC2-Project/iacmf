package org.iac2.service.architecturereconstruction.plugin.implementation.manual;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.iac2.common.Plugin;
import org.iac2.common.model.PluginConfigurationEntryDescriptor;
import org.iac2.common.model.PluginConfigurationEntryType;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPluginDescriptor;

public class ManualModelCreationPluginDescriptor implements ModelCreationPluginDescriptor {
    public static final String CONFIG_ENTRY_MODEL_PATH = "modelPath";
    public static final String IDENTIFIER = "manual-model-creation-plugin";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String getDescription() {
        return "This plugin allows creating an EDMM-based instance model using an external tool (such as Winery) or even " +
                "manually." +
                "The plugin only requires a URL to the EDMM file that represents the instance model. " +
                "This is especially helpful if there is still no model creation plugin for the used IaC technology, or if no IaC " +
                "technology for deployment management is used in the first place.";
    }

    @Override
    public Collection<PluginConfigurationEntryDescriptor> getConfigurationEntryDescriptors() {
        return List.of(
                new PluginConfigurationEntryDescriptor(
                        CONFIG_ENTRY_MODEL_PATH,
                        PluginConfigurationEntryType.URL,
                        true,
                        "The URL of the manually created EDMM instance model (must be accessible with HTTP GET)."
                )
        );
    }

    @Override
    public Plugin createPlugin() {
        return new ManualModelCreationPlugin(this);
    }

    @Override
    public Collection<String> getRequiredProductionSystemPropertyNames() {
        return Collections.emptyList();
    }

    @Override
    public boolean isIaCTechnologySupported(String iacTechnologyName) {
        // all iac technologies are supported since no iac tools are actually accessed by this plugin.
        return true;
    }
}
