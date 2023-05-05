package org.iac2.service.architecturereconstruction.plugin.implementation.kubernetes.model;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * Represents a relation between two container components in a Kubernetes deployment.
 */
@Getter
@Setter
public class ContainerComponentRelation {
    private String sourceId;
    private String targetId;
    private RelationType relationType;

    /**
     * Constructs a new ContainerComponentRelations object with the specified source ID, target ID, and relation type.
     * @param sourceId The ID of the source container component in the relation.
     * @param targetId The ID of the target container component in the relation.
     * @param relationType The type of relation.
     */
    public ContainerComponentRelation(String sourceId, String targetId, RelationType relationType) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.relationType = relationType;
    }
}