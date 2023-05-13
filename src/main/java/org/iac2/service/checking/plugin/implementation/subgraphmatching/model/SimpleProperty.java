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
        try {
            name = property.getName();
            value = property.getValue();
            type = property.getType();
        } catch (Exception ignored) {
        }
    }
}
