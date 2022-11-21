package org.iac2.service.checking.plugin.implementation.subgraphmatching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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

public abstract class RuleValidator {
    // todo rethink this if rules are to be modelled based on component names
    private static List<String> PROPERTIES_TO_IGNORE = new ArrayList<>();

    static {
        PROPERTIES_TO_IGNORE.add("name");
    }
    public static SubgraphMatchingRuleValidationResult validateComplianceRule(ComplianceRule rule, Graph<RootComponent, RootRelation> selector,
                                                                       Graph<RootComponent, RootRelation> checker) {
        final IsomorphismInspector<RootComponent, RootRelation> inspector1 = IsomorphismAlgorithmProvider.forRuleValidation(
                rule, selector, checker);
        List<GraphMapping<RootComponent, RootRelation>> ruleMappingList = IteratorUtils.toList(inspector1.getMappings());

        if (ruleMappingList.size() != 1) {
            return SubgraphMatchingRuleValidationResult.forOutcome(RuleValidationOutcome.NO_MAPPING_FROM_CHECKER_TO_SELECTOR);
        }

        for (RootComponent component:selector.vertexSet()) {
            if(!testPropertiesOfRuleComponent(component, rule)) {
                return SubgraphMatchingRuleValidationResult.forOutcome(RuleValidationOutcome.INVALID_PROPERTY_EXPRESSION);
            }
        }

        for (RootComponent component:checker.vertexSet()) {
            if(!testPropertiesOfRuleComponent(component, rule)) {
                return SubgraphMatchingRuleValidationResult.forOutcome(RuleValidationOutcome.INVALID_PROPERTY_EXPRESSION);
            }
        }

        GraphMapping<RootComponent, RootRelation> ruleMapping = ruleMappingList.get(0);
        SubgraphMatchingRuleValidationResult result = SubgraphMatchingRuleValidationResult.forOutcome(RuleValidationOutcome.VALID);
        result.setRuleMapping(ruleMapping);

        return result;
    }

    private static boolean testPropertiesOfRuleComponent(RootComponent component, ComplianceRule rule)  {
        try {
            Collection<Property> propertyCollection = component
                    .getProperties()
                    .values()
                    .stream()
                    .filter(p->!PROPERTIES_TO_IGNORE.contains(p.getName()))
                    .toList();
            for (Property p : propertyCollection) {
                String type = p.getType();
                String expression = p.getValue();

                if (expression != null && !expression.isEmpty()) {
                    Property property = createDummyProperty(p.getName(), type);
                    AttributeComparator.evaluateAttribute(expression, property, rule);
                }
            }

            return true;
        } catch (ExpressionException e) {
            return false;
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
