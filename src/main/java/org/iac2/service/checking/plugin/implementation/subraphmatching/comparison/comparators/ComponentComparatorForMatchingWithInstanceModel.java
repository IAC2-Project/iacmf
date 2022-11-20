package org.iac2.service.checking.plugin.implementation.subraphmatching.comparison.comparators;

import java.util.Collection;

import io.github.edmm.model.Property;
import io.github.edmm.model.component.RootComponent;
import lombok.Getter;
import org.apache.commons.text.CaseUtils;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.plugin.implementation.subraphmatching.comparison.comparators.attribute.AttributeComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionException;

@Getter
public class ComponentComparatorForMatchingWithInstanceModel implements  SemanticComponentComparator{
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentComparatorForMatchingWithInstanceModel.class);
    private final ComplianceRule rule;

    public ComponentComparatorForMatchingWithInstanceModel(ComplianceRule rule) {
        this.rule = rule;
    }

    public ComponentComparisonOutcome compare(RootComponent instanceModelComponent, RootComponent ruleComponent) {
        if (!ruleComponent.getClass().isAssignableFrom(instanceModelComponent.getClass())) {
            return ComponentComparisonOutcome.WRONG_TYPE;
        }

        String expression;
        String name;
        boolean isFound;
        Collection<Property> instanceModelComponentComputedProps = instanceModelComponent
                .getProperties()
                .values()
                .stream()
                .filter(Property::isComputed)
                .toList();

        // iterate over all properties of the compliance rule
        for (Property property : ruleComponent.getProperties().values()) {
            // only consider the properties that are assigned values
            if (property.isComputed()) {
                expression = property.getValue();
                name = property.getName();
                isFound = false;
                // iterate over all properties that are assigned values in the instance model
                for (Property instanceProperty : instanceModelComponentComputedProps) {
                    // find a property with a suitable name!
                    if (areEqualPropertyNames(name, instanceProperty.getName())) {
                        isFound = true;

                        try {
                            if (!AttributeComparator.evaluateAttribute(expression, instanceProperty, rule)) {
                                // the attribute values do not match!
                                return ComponentComparisonOutcome.WRONG_VALUE;
                            }
                        } catch (ExpressionException e) {
                            // expression does not have a boolean value! (we should have discovered this problem earlier!)
                            LOGGER.error("expression '{}' in property '{}' cannot be evaluated to a boolean value.", expression, name);
                            return ComponentComparisonOutcome.NOT_BOOLEAN;
                        }

                        break;
                    }
                }

                // a required property in the compliance rule is not found in the instance model component.
                if (!isFound) {
                    return ComponentComparisonOutcome.MISSING_PROPERTY;
                }
            }
        }

        // no problems found! the components match!
        return ComponentComparisonOutcome.MATCH;
    }

    private static boolean areEqualPropertyNames(String prop1, String prop2) {
        return convertToCamelCase(prop1).equals(convertToCamelCase(prop2));
    }

    private static String convertToCamelCase(String property) {
        return CaseUtils.toCamelCase(property, false, '_');
    }
}
