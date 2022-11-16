package org.iac2.service.architecturereconstruction.common.model.EdmmTypes;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;

public class Java11 extends SoftwareComponent {
    public static final Attribute<String> component_version = new Attribute<>("component_version", String.class);
    public static final Attribute<String> admin_credential = new Attribute<>("admin_credential", String.class);

    public Java11(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public void accept(ComponentVisitor v) {
        v.visit(this);
    }

    public String toString() {
        return "Java11()";
    }
}
