package org.iac2.service.architecturereconstruction.common.model.EdmmTypes;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;

public class MySqlDbms extends SoftwareComponent {
    public static final Attribute<String> DBMSUser = new Attribute<>("DBMSUser", String.class);
    public static final Attribute<String> DBMSPassword = new Attribute<>("DBMSPassword", String.class);
    public static final Attribute<String> DBMSPort = new Attribute<>("DBMSPort", String.class);
    public static final Attribute<String> root_password = new Attribute<>("root_password", String.class);
    public static final Attribute<String> port = new Attribute<>("port", String.class);

    public MySqlDbms(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public void accept(ComponentVisitor v) {
        v.visit(this);
    }

    public String toString() {
        return "MySqlDbms()";
    }
}
