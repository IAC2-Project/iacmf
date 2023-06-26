package org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.github.edmm.model.Property;
import io.github.edmm.model.component.RootComponent;
import lombok.Getter;
import org.apache.commons.text.CaseUtils;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison.attribute.AttributeComparator;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.model.SimpleProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionException;

@Getter
public class ComponentComparatorForMatchingWithInstanceModel implements SemanticComponentComparator {
    // todo rethink this if rules are to be modelled based on component names
    private final static List<String> PROPERTIES_TO_IGNORE = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentComparatorForMatchingWithInstanceModel.class);

    static {
        PROPERTIES_TO_IGNORE.add("name");
    }

    private final ComplianceRule rule;

    public ComponentComparatorForMatchingWithInstanceModel(ComplianceRule rule) {
        this.rule = rule;
    }

    private static boolean areEqualPropertyNames(String prop1, String prop2) {
        final String uniform1 = convertToCamelCase(prop1);
        final String uniform2 = convertToCamelCase(prop2);
        return uniform1.equalsIgnoreCase(uniform2);
    }

    private static String convertToCamelCase(String property) {
        return CaseUtils.toCamelCase(property, false, '_');
    }

    public ComponentComparisonResult compare(RootComponent instanceModelComponent, RootComponent ruleComponent) {
        if (!ruleComponent.getClass().isAssignableFrom(instanceModelComponent.getClass())) {
            return new ComponentComparisonResult(ComponentComparisonOutcome.WRONG_TYPE, null, null);
        }

        String expression;
        String name;
        boolean isFound;
        List<SimpleProperty> instanceModelComponentProps = new ArrayList<>(instanceModelComponent
                .getProperties()
                .values()
                .stream()
                .map(SimpleProperty::new)
                .toList());
        instanceModelComponentProps.add(new SimpleProperty("componentName", "string", instanceModelComponent.getName()));

        // iterate over all properties of the compliance rule
        for (Property property : ruleComponent.getProperties().values()) {
            // only consider the properties that are assigned values
            // here we cannot use "isComputed" since it is somehow always false when we rebuild the graph from a YAML string!
            expression = property.getValue();
            name = property.getName();

            if (expression != null && !expression.isEmpty() && !PROPERTIES_TO_IGNORE.contains(name)) {
                isFound = false;
                // iterate over all properties that are assigned values in the instance model
                for (SimpleProperty instanceProperty : instanceModelComponentProps) {
                    // find a property with a suitable name!
                    if (areEqualPropertyNames(name, instanceProperty.getName())) {
                        isFound = true;

                        try {
                            if (!AttributeComparator.evaluateAttribute(expression, instanceProperty, rule)) {
                                // the attribute values do not match!
                                return new ComponentComparisonResult(ComponentComparisonOutcome.WRONG_VALUE, name, expression);
                            }
                        } catch (ExpressionException e) {
                            // expression does not have a boolean value! (we should have discovered this problem earlier!)
                            LOGGER.error("expression '{}' in property '{}' cannot be evaluated to a boolean value.", expression, name);
                            return new ComponentComparisonResult(ComponentComparisonOutcome.NOT_BOOLEAN, name, expression);
                        }
                        LOGGER.info("Property '{}.{}' with value '{}' matches property '{}.{}' with value '{}'",
                                instanceModelComponent.getName(),
                                instanceProperty.getName(),
                                instanceProperty.getValue(),
                                ruleComponent.getName(),
                                property.getName(),
                                property.getValue());
                        break;
                    }
                }

                // a required property in the compliance rule is not found in the instance model component.
                if (!isFound) {
                    return new ComponentComparisonResult(ComponentComparisonOutcome.MISSING_PROPERTY, name, null);
                }
            }
        }

        // no problems found! the components match!
        return new ComponentComparisonResult(ComponentComparisonOutcome.MATCH, null, null);
    }
}
