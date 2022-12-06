package org.iac2.service.utility;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;

public class TestComponentType extends SoftwareComponent {

    public static final Attribute<String> WOW = new Attribute<>("wow", String.class);

    public TestComponentType(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public void accept(ComponentVisitor v) {
        v.visit(this);
    }

    public String toString() {
        return "TestComponentType()";
    }
}
