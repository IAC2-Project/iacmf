package org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison.attribute;

import io.github.edmm.model.Property;

import java.util.Arrays;
import java.util.List;

public class RuleAttributeEvaluationContext<T> {
    private final T value;

    private RuleAttributeEvaluationContext(T propertyValue) {
        this.value = propertyValue;
    }

    public static RuleAttributeEvaluationContext<?> forProperty(Property property) {
        switch (property.getType()) {
            default:
                return new RuleAttributeEvaluationContext<>(property.getValue());
            case "integer":
                return new RuleAttributeEvaluationContext<>(Integer.parseInt(property.getValue()));
            case "float":
                return new RuleAttributeEvaluationContext<>(Float.parseFloat(property.getValue()));
            case "list":
                List<String> list = Arrays.stream(property.getValue().split(",")).map(String::trim).toList();
                return new RuleAttributeEvaluationContext<>(list);
        }
    }

    public T getValue() {
        return value;
    }

}
