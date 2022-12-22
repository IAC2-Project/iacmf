package org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison.comparators.attribute;

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.SoftwareComponent;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.utility.Edmm;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison.attribute.AttributeComparator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ExpressionException;

import java.util.List;
import java.util.Map;

class AttributeComparatorTest {

    @Test
    void testExpressionNoVariables() throws IllegalAccessException {
        ComplianceRule rule = new ComplianceRule(1L, "isomeso", "somewhere");
        EntityGraph instanceModel = new EntityGraph();

        Edmm.addComponent(instanceModel, "comp1", Map.of("structuralState", "EXPECTED"), SoftwareComponent.class);
        DeploymentModel model = new DeploymentModel("test", instanceModel);
        RootComponent theComponent = model.getComponent("comp1").orElseThrow();
        Property property = theComponent.getProperty("structuralState").orElseThrow();
        Assertions.assertEquals("EXPECTED", property.getValue());
        Assertions.assertTrue(AttributeComparator.evaluateAttribute("'EXPECTED'.equals(value)", property, rule));
        Assertions.assertFalse(AttributeComparator.evaluateAttribute("!'EXPECTED'.equals(value)", property, rule));
        Assertions.assertThrows(EvaluationException.class, () -> AttributeComparator.evaluateAttribute("1+1", property, rule));
    }

    @Test
    void testExpressionWithVariables() throws IllegalAccessException {
        ComplianceRule rule = new ComplianceRule(1L, "isomeso", "somewhere");
        rule.addStringParameter("HOST", "http://localhost");
        rule.addIntParameter("PORT", 1234);
        EntityGraph instanceModel = new EntityGraph();

        Edmm.addComponent(instanceModel, "comp1", Map.of("url", "http://localhost:1234"), SoftwareComponent.class);
        DeploymentModel model = new DeploymentModel("test", instanceModel);
        RootComponent theComponent = model.getComponent("comp1").orElseThrow();
        Property property = theComponent.getProperty("url").orElseThrow();
        Assertions.assertTrue(AttributeComparator.evaluateAttribute("#HOST.concat(':').concat(T(String).valueOf(#PORT)).equals(value)", property, rule));
        Assertions.assertTrue(AttributeComparator.evaluateAttribute("(#HOST + ':' + #PORT).equals(value)", property, rule));
    }

    @Test
    void testMissingVariables() throws IllegalAccessException {
        ComplianceRule rule = new ComplianceRule(1L, "isomeso", "somewhere");
        rule.addStringParameter("HOST", "http://localhost");
        EntityGraph instanceModel = new EntityGraph();

        Edmm.addComponent(instanceModel, "comp1", Map.of("url", "http://localhost:1234"), SoftwareComponent.class);
        DeploymentModel model = new DeploymentModel("test", instanceModel);
        RootComponent theComponent = model.getComponent("comp1").orElseThrow();
        Property property = theComponent.getProperty("url").orElseThrow();
        Assertions.assertFalse(AttributeComparator.evaluateAttribute("(#HOST + ':' + #PORT).equals(value)", property, rule));
    }

    @Test
    void testNotBoolean() throws IllegalAccessException {
        ComplianceRule rule = new ComplianceRule(1L, "isomeso", "somewhere");
        rule.addStringParameter("HOST", "http://localhost");
        rule.addIntParameter("PORT", 1234);
        EntityGraph instanceModel = new EntityGraph();

        Edmm.addComponent(instanceModel, "comp1", Map.of("url", "http://localhost:1234"), SoftwareComponent.class);
        DeploymentModel model = new DeploymentModel("test", instanceModel);
        RootComponent theComponent = model.getComponent("comp1").orElseThrow();
        Property property = theComponent.getProperty("url").orElseThrow();
        Assertions.assertThrows(ExpressionException.class, () -> AttributeComparator.evaluateAttribute("(#HOST + ':' + #PORT)", property, rule));
    }

    @Test
    void testListContainment() throws IllegalAccessException {
        ComplianceRule rule = new ComplianceRule(1L, "isomeso", "somewhere");
        rule.addStringCollectionParameter("ALLOWED", List.of("A", "B"));
        EntityGraph instanceModel = new EntityGraph();

        EntityId id = Edmm.addComponent(instanceModel, "comp1", Map.of("p1", List.of("A")), SoftwareComponent.class);
        DeploymentModel model = new DeploymentModel("test", instanceModel);
        RootComponent theComponent = model.getComponent("comp1").orElseThrow();
        Property property = theComponent.getProperty("p1").orElseThrow();
        Assertions.assertTrue(AttributeComparator.evaluateAttribute("#ALLOWED.containsAll(value)", property, rule));

        Edmm.addPropertyAssignments(instanceModel, id, Map.of("p2", List.of("B", "A")));
        model = new DeploymentModel("test", instanceModel);
        theComponent = model.getComponent("comp1").orElseThrow();
        property = theComponent.getProperty("p2").orElseThrow();
        Assertions.assertTrue(AttributeComparator.evaluateAttribute("#ALLOWED.containsAll(value)", property, rule));

        Edmm.addPropertyAssignments(instanceModel, id, Map.of("p3", List.of("B", "C")));
        model = new DeploymentModel("test", instanceModel);
        theComponent = model.getComponent("comp1").orElseThrow();
        property = theComponent.getProperty("p3").orElseThrow();
        Assertions.assertFalse(AttributeComparator.evaluateAttribute("#ALLOWED.containsAll(value)", property, rule));
        Assertions.assertTrue(AttributeComparator.evaluateAttribute("{'C', 'B', 'X'}.containsAll(value)", property, rule));
    }
}
