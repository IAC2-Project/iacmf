package org.iac2.service.architecturereconstruction.plugin.implementation.kubernetes;

import org.iac2.common.Plugin;
import org.iac2.common.model.PluginConfigurationEntryDescriptor;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPluginDescriptor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class KubernetesModelCreationPluginDescriptor implements ModelCreationPluginDescriptor {

    public static final String IDENTIFIER = "kubernetes-model-creation-plugin";

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String getDescription() {
        return "This plugin creates an instance model for a cloud application deployed and managed by the Kubernetes " +
                "Container Orchestration technology (https://kubernetes.io/).";
    }

    @Override
    public Collection<PluginConfigurationEntryDescriptor> getConfigurationEntryDescriptors() {
        return Collections.emptyList();
    }

    @Override
    public Plugin createPlugin() {
        return new KubernetesModelCreationPlugin(this);
    }

    @Override
    public boolean isIaCTechnologySupported(String iacTechnologyName) {
        return iacTechnologyName.equalsIgnoreCase("kubernetes");
    }

    @Override
    public Collection<String> getRequiredProductionSystemPropertyNames() {
        return List.of(
                "kubeConfigPath",
                "namespace"
        );
    }
}
