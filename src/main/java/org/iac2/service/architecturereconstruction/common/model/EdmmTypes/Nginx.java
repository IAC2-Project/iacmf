package org.iac2.service.architecturereconstruction.common.model.EdmmTypes;

import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.component.WebServer;
import io.github.edmm.model.visitor.ComponentVisitor;

public class Nginx extends WebServer {

    public Nginx(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    public void accept(ComponentVisitor v) {
        v.visit(this);
    }

    public String toString() {
        return "Nginx()";
    }
}
