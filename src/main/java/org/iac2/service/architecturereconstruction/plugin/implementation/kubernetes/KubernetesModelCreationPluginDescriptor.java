package org.iac2.service.architecturereconstruction.plugin.implementation.kubernetes;

import org.iac2.common.Plugin;
import org.iac2.common.model.PluginConfigurationEntryDescriptor;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPluginDescriptor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a plugin descriptor for creating an instance model for a cloud application deployed.
 * It supports Kubernetes Container Orchestration technology.
 */
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

    /**
     * Check whether the given infrastructure-as-code (IaC) technology name is supported by this plugin descriptor.
     *
     * @param iacTechnologyName The name of the IaC technology to check.
     * @return True if the IaC technology is supported, false otherwise.
     */
    @Override
    public boolean isIaCTechnologySupported(String iacTechnologyName) {
        return iacTechnologyName.equalsIgnoreCase("kubernetes");
    }


    /**
     * Get the names of the required production system properties for this plugin descriptor.
     *
     * @return A list of the required production system property names.
     */
    @Override
    public Collection<String> getRequiredProductionSystemPropertyNames() {
        return List.of(
                "kubeConfigPath",
                "namespace"
        );
    }
}
