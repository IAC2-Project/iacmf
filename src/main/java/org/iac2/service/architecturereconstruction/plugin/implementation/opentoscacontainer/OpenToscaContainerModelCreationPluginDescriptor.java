package org.iac2.service.architecturereconstruction.plugin.implementation.opentoscacontainer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.iac2.common.Plugin;
import org.iac2.common.model.PluginConfigurationEntryDescriptor;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPluginDescriptor;

public class OpenToscaContainerModelCreationPluginDescriptor implements ModelCreationPluginDescriptor {

    public static final String IDENTIFIER = "opentosca-container-model-creation-plugin";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String getDescription() {
        return "This plugin creates an instance model for a cloud application deployed and managed by the OpenTOSCA " +
                "Container IaC technology (http://opentosca.github.io/container/).";
    }

    @Override
    public Collection<PluginConfigurationEntryDescriptor> getConfigurationEntryDescriptors() {
        return Collections.emptyList();
    }

    @Override
    public Plugin createPlugin() {
        return new OpenToscaContainerModelCreationPlugin(this);
    }

    @Override
    public boolean isIaCTechnologySupported(String iacTechnologyName) {
        return iacTechnologyName.equalsIgnoreCase("opentoscacontainer");
    }

    @Override
    public Collection<String> getRequiredProductionSystemPropertyNames() {
        return List.of(
                "opentoscacontainer_hostname",
                "opentoscacontainer_port",
                "opentoscacontainer_appId",
                "opentoscacontainer_instanceId"
        );
    }
}
