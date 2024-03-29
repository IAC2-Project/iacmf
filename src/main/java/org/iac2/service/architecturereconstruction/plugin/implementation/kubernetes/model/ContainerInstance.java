package org.iac2.service.architecturereconstruction.plugin.implementation.kubernetes.model;

import io.kubernetes.client.models.V1EnvVar;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 * Represents an instance of a container running in a Kubernetes environment.
 */
@Getter
@Setter
public class ContainerInstance {

    private String containerId;
    private String containerName;
    private String containerType;
    private String containerNamespace;
    private List<V1EnvVar> containerEnvVar;

    public ContainerInstance(String containerId, String containerName, String containerType, String containerNamespace,List<V1EnvVar> containerEnvVar) {
        this.containerId = containerId;
        this.containerName = containerName;
        this.containerType = containerType;
        this.containerNamespace = containerNamespace;
        this.containerEnvVar = containerEnvVar;
    }

}
