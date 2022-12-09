package org.iac2.service.checking.plugin.implementation.subgraphmatching;

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.core.parser.support.DefaultKeys;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.apache.commons.collections4.IteratorUtils;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.utility.Edmm;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison.attribute.AttributeComparator;
import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.isomorphism.IsomorphismInspector;
import org.springframework.expression.ExpressionException;

import java.util.*;

public abstract class RuleValidator {
    // todo rethink this if rules are to be modelled based on component names
    private static final List<String> PROPERTIES_TO_IGNORE = new ArrayList<>();

    static {
        PROPERTIES_TO_IGNORE.add("name");
    }

    public static SubgraphMatchingRuleValidationResult validateComplianceRule(ComplianceRule rule, Graph<RootComponent, RootRelation> selector,
                                                                              Graph<RootComponent, RootRelation> checker) {
        final IsomorphismInspector<RootComponent, RootRelation> inspector1 = IsomorphismAlgorithmProvider.forRuleValidation(
                rule, selector, checker);
        List<GraphMapping<RootComponent, RootRelation>> ruleMappingList = IteratorUtils.toList(inspector1.getMappings());

        // check that there is exactly one mapping from the checker to the selector
        if (ruleMappingList.size() != 1) {
            return new SubgraphMatchingRuleValidationResult(RuleValidationOutcome.NO_MAPPING_FROM_CHECKER_TO_SELECTOR);
        }

        // check properties (expressions) of all nodes in the rule (checker and selector)
        Set<RootComponent> allNodes = new HashSet<>(selector.vertexSet());
        allNodes.addAll(checker.vertexSet());

        try {
            for (RootComponent component : allNodes) {
                validatePropertiesOfRuleComponent(component, rule);
            }
        } catch (RuntimeException e) {
            return new SubgraphMatchingRuleValidationResult(e.getMessage(),
                    RuleValidationOutcome.INVALID_PROPERTY_EXPRESSION, null);
        }

        // everything is fine!
        return new SubgraphMatchingRuleValidationResult(RuleValidationOutcome.VALID, ruleMappingList.get(0));
    }

    private static void validatePropertiesOfRuleComponent(RootComponent component, ComplianceRule rule)
            throws ExpressionException, RuntimeException {
        // if something goes wrong here, an ExpressionException is thrown.
        try {
            Collection<Property> propertyCollection = component
                    .getProperties()
                    .values()
                    .stream()
                    .filter(p -> !PROPERTIES_TO_IGNORE.contains(p.getName()))
                    .toList();
            for (Property p : propertyCollection) {
                String type = p.getType();
                String expression = p.getValue();

                if (expression != null && !expression.isEmpty()) {
                    Property property = createDummyProperty(p.getName(), type);
                    AttributeComparator.evaluateAttribute(expression, property, rule);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Property createDummyProperty(String name, String type) throws IllegalAccessException {
        EntityGraph graph = new EntityGraph();
        EntityId c = Edmm.addComponent(graph, "dummy", new HashMap<>(), RootComponent.class);
        String dummyValue = switch (type) {
            case "list" -> "a,b";
            case DefaultKeys.INTEGER -> "1";
            case DefaultKeys.FLOAT -> "2.0";
            case "boolean" -> "true";
            default -> "abc";
        };
        Edmm.addPropertyExpressionAssignment(graph, c, name, type, dummyValue);
        DeploymentModel model = new DeploymentModel("dummy-dm", graph);
        RootComponent component = model.getComponent(c.getName()).orElseThrow();
        return component.getProperty(name).orElseThrow();
    }
}
