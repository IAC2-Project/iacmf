package org.iac2.service.architecturereconstruction.plugin.implementation.manual;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.iac2.common.Plugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPluginDescriptor;

public class ManualModelCreationPluginDescriptor implements ModelCreationPluginDescriptor {
    public static final String IDENTIFIER = "manual-model-creation-plugin";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Collection<String> getRequiredConfigurationEntryNames() {
        return List.of(ManualModelCreationPlugin.CONFIG_ENTRY_MODEL_PATH);
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
