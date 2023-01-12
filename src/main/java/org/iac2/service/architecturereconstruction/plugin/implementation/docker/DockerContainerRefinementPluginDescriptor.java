package org.iac2.service.architecturereconstruction.plugin.implementation.docker;

import java.util.Collection;

import org.apache.commons.compress.utils.Lists;
import org.iac2.common.Plugin;
import org.iac2.common.model.PluginConfigurationEntryDescriptor;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelRefinementPluginDescriptor;

public class DockerContainerRefinementPluginDescriptor implements ModelRefinementPluginDescriptor {
    public static final String IDENTIFIER = "docker-refinement-plugin";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String getDescription() {
        return "This plugin allows identifying which reachable docker containers were expected or unexpected according " +
                "to the original instance model (i.e., before applying this plugin). Furthermore, it helps in detecting " +
                "unexpectedly removed docker containers.";
    }

    @Override
    public Collection<PluginConfigurationEntryDescriptor> getConfigurationEntryDescriptors() {
        return Lists.newArrayList();
    }

    @Override
    public Plugin createPlugin() {
        return new DockerContainerRefinementPlugin(this);
    }

    @Override
    public Collection<String> getRequiredProductionSystemPropertyNames() {
        return Lists.newArrayList();
    }
}
