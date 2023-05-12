package org.iac2.service.checking.plugin.implementation.subgraphmatching.model;

import io.github.edmm.model.Property;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleProperty {
    private String name;
    private String type;
    private String value;

    public SimpleProperty(Property property) {
        name = property.getName();
        type = property.getType();
        value = property.getValue();
    }
}
