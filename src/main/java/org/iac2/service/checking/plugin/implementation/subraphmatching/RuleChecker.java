package org.iac2.service.checking.plugin.implementation.subraphmatching;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.core.parser.support.DefaultKeys;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.apache.commons.collections4.IteratorUtils;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.utility.Edmm;
import org.iac2.service.checking.plugin.implementation.subraphmatching.comparison.comparators.ComponentComparisonOutcome;
import org.iac2.service.checking.plugin.implementation.subraphmatching.comparison.comparators.SemanticComponentComparator;
import org.iac2.service.checking.plugin.implementation.subraphmatching.comparison.comparators.attribute.AttributeComparator;
import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.isomorphism.IsomorphismInspector;
import org.springframework.expression.ExpressionException;

public class RuleChecker {
    private final Graph<RootComponent, RootRelation> instanceModel;

    public RuleChecker(InstanceModel instanceModel) {
        this.instanceModel = EdmmGraphCreator.of(instanceModel.getDeploymentModel());
    }

    public RuleCheckingResult checkCompliance(ComplianceRule rule, Graph<RootComponent, RootRelation> selector,
                                              Graph<RootComponent, RootRelation> checker) {
        RuleValidationResult validationResult = validateComplianceRule(rule, selector, checker);

        if (validationResult.getOutcome() == RuleValidationOutcome.VALID) {
            final GraphMapping<RootComponent, RootRelation> ruleMapping = validationResult.getRuleMapping();
            final IsomorphismInspector<RootComponent, RootRelation> inspector = IsomorphismAlgorithmProvider.forSelection(
                    rule, instanceModel, selector);
            final SemanticComponentComparator semanticComparator = IsomorphismAlgorithmProvider.getSemanticComponentComparator(rule);

            Iterator<GraphMapping<RootComponent, RootRelation>> iterator = inspector.getMappings();

            while (iterator.hasNext()) {
                GraphMapping<RootComponent, RootRelation> mappingToInstanceModel = iterator.next();

                for (RootComponent selectorComponent : selector.vertexSet()) {
                    RootComponent instanceModelComponent = mappingToInstanceModel.getVertexCorrespondence(selectorComponent, false);
                    RootComponent checkerComponent = ruleMapping.getVertexCorrespondence(selectorComponent, false);
                    ComponentComparisonOutcome outcome = semanticComparator.compare(instanceModelComponent, checkerComponent);

                    if (outcome != ComponentComparisonOutcome.MATCH) {
                        RuleCheckingResult result = RuleCheckingResult.forOutcome(RuleCheckingOutcome.COMPLIANCE_VIOLATION);
                        result.setDetails(checkerComponent, instanceModelComponent, String.valueOf(outcome));

                        return result;
                    }
                }
            }

            return RuleCheckingResult.forOutcome(RuleCheckingOutcome.NO_VIOLATIONS);
        }

        RuleCheckingResult result = RuleCheckingResult.forOutcome(RuleCheckingOutcome.INVALID_RULE);
        result.setDetails(null, null, String.valueOf(validationResult.getOutcome()));

        return result;
    }

    public RuleValidationResult validateComplianceRule(ComplianceRule rule, Graph<RootComponent, RootRelation> selector,
                                                       Graph<RootComponent, RootRelation> checker) {
        final IsomorphismInspector<RootComponent, RootRelation> inspector1 = IsomorphismAlgorithmProvider.forRuleValidation(
                rule, selector, checker);
        List<GraphMapping<RootComponent, RootRelation>> ruleMappingList = IteratorUtils.toList(inspector1.getMappings());

        if (ruleMappingList.size() != 1) {
            return RuleValidationResult.forOutcome(RuleValidationOutcome.NO_MAPPING_FROM_CHECKER_TO_SELECTOR);
        }

        for (RootComponent component:selector.vertexSet()) {
            if(!testPropertiesOfRuleComponent(component, rule)) {
                return RuleValidationResult.forOutcome(RuleValidationOutcome.INVALID_PROPERTY_EXPRESSION);
            }
        }

        for (RootComponent component:checker.vertexSet()) {
            if(!testPropertiesOfRuleComponent(component, rule)) {
                return RuleValidationResult.forOutcome(RuleValidationOutcome.INVALID_PROPERTY_EXPRESSION);
            }
        }

        GraphMapping<RootComponent, RootRelation> ruleMapping = ruleMappingList.get(0);
        RuleValidationResult result = RuleValidationResult.forOutcome(RuleValidationOutcome.VALID);
        result.setRuleMapping(ruleMapping);

        return result;
    }

    private boolean testPropertiesOfRuleComponent(RootComponent component, ComplianceRule rule)  {
        try {
            for (Property p : component.getProperties().values()) {
                String type = p.getType();
                String expression = p.getValue();

                if (expression != null) {
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

    private Property createDummyProperty(String name, String type) throws IllegalAccessException {
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
