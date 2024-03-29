package org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison.comparators;

import java.util.HashMap;

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.Database;
import io.github.edmm.model.component.MysqlDatabase;
import io.github.edmm.model.component.Paas;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.utility.Edmm;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison.ComponentComparatorForMatchingWithInstanceModel;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.comparison.ComponentComparisonOutcome;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ComponentComparatorForMatchingWithInstanceModelTest {
    // rule component
    Database db;

    // instance model components
    MysqlDatabase mysqlDatabase;
    Paas paas;

    // comparator
    ComponentComparatorForMatchingWithInstanceModel comparator;

    @BeforeEach
    void init() throws IllegalAccessException {
        EntityGraph graph1 = new EntityGraph();
        EntityGraph graph2 = new EntityGraph();
        EntityId dbId = Edmm.addComponent(graph1, "a", new HashMap<>(), Database.class);
        EntityId mySqlId = Edmm.addComponent(graph2, "b", new HashMap<>(), MysqlDatabase.class);
        EntityId paasId = Edmm.addComponent(graph2, "paas", new HashMap<>(), Paas.class);
        DeploymentModel ruleModel = new DeploymentModel("rule", graph1);
        DeploymentModel instanceModel = new DeploymentModel("instance model", graph2);
        ComplianceRule rule = new ComplianceRule(1L, "a", "b", "dummy");

        db = (Database) ruleModel.getComponent(dbId.getName()).orElseThrow();
        mysqlDatabase = (MysqlDatabase) instanceModel.getComponent(mySqlId.getName()).orElseThrow();
        paas = (Paas) instanceModel.getComponent(paasId.getName()).orElseThrow();
        comparator = new ComponentComparatorForMatchingWithInstanceModel(rule);
    }

    @Test
    void testSimpleComponents() {
        Assertions.assertEquals(ComponentComparisonOutcome.MATCH, comparator.compare(mysqlDatabase, db).outcome());
        Assertions.assertEquals(ComponentComparisonOutcome.WRONG_TYPE, comparator.compare(paas, db).outcome());
    }

    @Test
    void testMissingProperty() {
        db.addProperty("a", "'apple'.equals(value)");
        Assertions.assertEquals(ComponentComparisonOutcome.MISSING_PROPERTY, comparator.compare(mysqlDatabase, db).outcome());
        mysqlDatabase.addProperty("b", "apple");
        Assertions.assertEquals(ComponentComparisonOutcome.MISSING_PROPERTY, comparator.compare(mysqlDatabase, db).outcome());
        mysqlDatabase.addProperty("a", "apple");
        Assertions.assertEquals(ComponentComparisonOutcome.MATCH, comparator.compare(mysqlDatabase, db).outcome());
    }

    @Test
    void testNotBoolean() {
        db.addProperty("a", "value.length()");
        mysqlDatabase.addProperty("a", "apple");
        Assertions.assertEquals(ComponentComparisonOutcome.NOT_BOOLEAN, comparator.compare(mysqlDatabase, db).outcome());
    }

    @Test
    void testWrongValue() {
        db.addProperty("a", "'apple'.equals(value)");
        mysqlDatabase.addProperty("a", "banana");
        Assertions.assertEquals(ComponentComparisonOutcome.WRONG_VALUE, comparator.compare(mysqlDatabase, db).outcome());
    }

    @Test
    void testVariables() {
        comparator.getRule().addStringParameter("HOST", "https://iac2.com/iacmf");
        db.addProperty("a", "#HOST.equals(value)");
        mysqlDatabase.addProperty("a", "https://iac2.com/iacmf");
        Assertions.assertEquals(ComponentComparisonOutcome.MATCH, comparator.compare(mysqlDatabase, db).outcome());
    }
}
