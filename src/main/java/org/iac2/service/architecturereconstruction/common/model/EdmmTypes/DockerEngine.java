package org.iac2.service.architecturereconstruction.common.model.EdmmTypes;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;

public class DockerEngine extends RootComponent {

    public static final Attribute<String> DOCKER_ENGINE_URL = new Attribute<>("DockerEngineURL", String.class);
    public static final Attribute<String> DOCKER_ENGINE_CERTIFICATE = new Attribute<>("DockerEngineCertificate", String.class);


    public DockerEngine(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public void accept(ComponentVisitor v) {
        v.visit(this);
    }

    public String toString() {
        return "DockerEngine()";
    }
}
