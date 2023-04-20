package org.iac2.service.architecturereconstruction.common.model.Kubernetes;

import io.kubernetes.client.models.V1EnvVar;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
