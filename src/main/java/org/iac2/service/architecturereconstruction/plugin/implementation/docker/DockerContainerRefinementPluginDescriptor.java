package org.iac2.service.architecturereconstruction.plugin.implementation.docker;

import java.util.Collection;

import org.apache.commons.compress.utils.Lists;
import org.iac2.common.Plugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelRefinementPluginDescriptor;

public class DockerContainerRefinementPluginDescriptor implements ModelRefinementPluginDescriptor {
    public static final String IDENTIFIER = "docker-enhancement-plugin";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Collection<String> getRequiredConfigurationEntryNames() {
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
