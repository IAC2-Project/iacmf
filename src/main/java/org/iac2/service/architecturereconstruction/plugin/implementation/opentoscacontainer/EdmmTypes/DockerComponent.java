package org.iac2.service.architecturereconstruction.plugin.implementation.opentoscacontainer.EdmmTypes;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;

public class DockerComponent extends SoftwareComponent {
    public static final Attribute<String> STATE = new Attribute<>("state", String.class);
    public static final Attribute<String> CONTAINER_ID = new Attribute<>("container_id", String.class);

    public DockerComponent(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public void accept(ComponentVisitor v) {
        v.visit(this);
    }

    public String toString() {
        return "DockerComponent()";
    }
}
