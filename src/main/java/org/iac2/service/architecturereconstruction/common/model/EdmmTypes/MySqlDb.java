package org.iac2.service.architecturereconstruction.common.model.EdmmTypes;

import java.util.Collection;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.component.Database;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;

public class MySqlDb extends Database {
    public static final Attribute<String> name = new Attribute<>("name", String.class);
    public static final Attribute<String> port = new Attribute<>("port", String.class);
    public static final Attribute<String> DBName = new Attribute<>("DBName", String.class);
    public static final Attribute<String> DBUser = new Attribute<>("DBUser", String.class);
    public static final Attribute<String> DBPassword = new Attribute<>("DBPassword", String.class);
    public static final Attribute<Collection> users = new Attribute<>("users", Collection.class);

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
