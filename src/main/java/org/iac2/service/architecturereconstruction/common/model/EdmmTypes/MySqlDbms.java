package org.iac2.service.architecturereconstruction.common.model.EdmmTypes;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.component.Dbms;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.visitor.ComponentVisitor;

public class MySqlDbms extends Dbms {
    public static final Attribute<String> DBMSUser = new Attribute<>("DBMSUser", String.class);
    public static final Attribute<String> DBMSPassword = new Attribute<>("DBMSPassword", String.class);
    public static final Attribute<String> DBMSPort = new Attribute<>("DBMSPort", String.class);

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
