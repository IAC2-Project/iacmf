package org.iac2.service.architecturereconstruction.common.model.EdmmTypes;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;

public class MySqlDb extends SoftwareComponent {
    public static final Attribute<String> name = new Attribute<>("name", String.class);
    public static final Attribute<String> port = new Attribute<>("port", String.class);
    public static final Attribute<String> user = new Attribute<>("user", String.class);
    public static final Attribute<String> password = new Attribute<>("password", String.class);
    public static final Attribute<String> DBName = new Attribute<>("DBName", String.class);
    public static final Attribute<String> DBUser = new Attribute<>("DBUser", String.class);
    public static final Attribute<String> DBPassword = new Attribute<>("DBPassword", String.class);

    public MySqlDb(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public void accept(ComponentVisitor v) {
        v.visit(this);
    }

    public String toString() {
        return "MySqlDb()";
    }
}
