package org.iac2.service.checking.plugin.implementation.subraphmatching.comparison.comparators.attribute;

import java.util.Map;

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.SoftwareComponent;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.utility.Edmm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AttributeComparatorTest {

    @Test
    void testExpressionNoVariables() throws IllegalAccessException {
        ComplianceRule rule = new ComplianceRule("isomeso", "somewhere");
        EntityGraph instanceModel = new EntityGraph();

        Edmm.addComponent(instanceModel, "comp1", Map.of("structuralState", "EXPECTED"), SoftwareComponent.class);
        DeploymentModel model = new DeploymentModel("test", instanceModel);
        RootComponent theComponent = model.getComponent("comp1").orElseThrow();
        Property property = theComponent.getProperty("structuralState").orElseThrow();
        Assertions.assertEquals("EXPECTED", property.getValue());
        Assertions.assertTrue(AttributeComparator.evaluateAttribute("'EXPECTED'.equals(value)", property, rule));
        Assertions.assertFalse(AttributeComparator.evaluateAttribute("!'EXPECTED'.equals(value)", property, rule));
    }
}