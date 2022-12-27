package org.iac2.service.architecturereconstruction.plugin.implementation.manual;

import java.util.Collection;
import java.util.Collections;

import org.iac2.common.Plugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPluginDescriptor;

public class ManualModelCreationPluginDescriptor implements ModelCreationPluginDescriptor {
    public static final String IDENTIFIER = "manualplugin";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Collection<String> getRequiredConfigurationEntryNames() {
        return Collections.emptyList();
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
        return iacTechnologyName.equalsIgnoreCase("opentoscacontainer");
    }
}
