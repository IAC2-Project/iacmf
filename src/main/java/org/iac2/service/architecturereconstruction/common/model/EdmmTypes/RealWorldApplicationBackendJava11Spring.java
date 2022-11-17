package org.iac2.service.architecturereconstruction.common.model.EdmmTypes;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;

// please don't ask me why the type is this "elaborate"
public class RealWorldApplicationBackendJava11Spring extends SoftwareComponent {
    public static final Attribute<String> AppName = new Attribute<>("AppName", String.class);
    public static final Attribute<String> Port = new Attribute<>("Port", String.class);
    public static final Attribute<String> context_root = new Attribute<>("context_root", String.class);

    public RealWorldApplicationBackendJava11Spring(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public void accept(ComponentVisitor v) {
        v.visit(this);
    }

    public String toString() {
        return "RealWorldApplicationBackendJava11Spring()";
    }
}
