package org.iac2.service.architecturereconstruction.common.model.Kubernetes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerComponentRelations {
    private String sourceId;
    private String targetId;
    private String containerName;
    private String relationType;
    private String containerType;

    public ContainerComponentRelations(String sourceId, String targetId, String relationType) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.relationType = relationType;
    }
}