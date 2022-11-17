package org.iac2.service.checking.plugin.implementation.subraphmatching;

import java.util.HashMap;

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.Go;
import io.github.edmm.model.component.MysqlDbms;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.component.Tomcat;
import io.github.edmm.model.component.WebServer;
import io.github.edmm.model.relation.HostedOn;
import io.github.edmm.model.relation.RootRelation;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.utility.Edmm;
import org.iac2.common.utility.EdmmTypeResolver;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerContainer;
import org.jgrapht.Graph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RuleCheckerTest {

    @Test
    void testInvalidRule() throws IllegalAccessException {
        EdmmTypeResolver.putMapping("docker_container", DockerContainer.class);
        final EntityGraph selector1 = new EntityGraph();
        final EntityId app1Id = Edmm.addComponent(selector1, "app-1", new HashMap<>(), SoftwareComponent.class);
        final EntityId tomcatId = Edmm.addComponent(selector1, "tomcat1", new HashMap<>(), Tomcat.class);
        Edmm.addRelation(selector1, app1Id, tomcatId, HostedOn.class);
        final Graph<RootComponent, RootRelation> selectorGraph = EdmmGraphCreator.of(selector1);

        final EntityGraph checker1 = new EntityGraph();
        final EntityId app2Id = Edmm.addComponent(checker1, "app-2", new HashMap<>(), SoftwareComponent.class);
        final EntityId goId = Edmm.addComponent(checker1, "go1", new HashMap<>(), Go.class);
        Edmm.addRelation(checker1, app2Id, goId, HostedOn.class);
        final Graph<RootComponent, RootRelation> checkerGraph = EdmmGraphCreator.of(checker1);

        final EntityGraph instanceModelGraph = new EntityGraph();
        Edmm.addComponent(instanceModelGraph, "paas1", new HashMap<>(), Paas.class);
        final InstanceModel instanceModel = new InstanceModel(new DeploymentModel("test3", instanceModelGraph));

        final ComplianceRule rule = new ComplianceRule("cr-1", "subgraph-matching");

        RuleChecker checker = new RuleChecker(instanceModel);
        RuleCheckingResult result = checker.checkCompliance(rule, selectorGraph, checkerGraph);
        Assertions.assertEquals(Outcome.INVALID_RULE, result.getOutcome());

        final EntityGraph selector2 = new EntityGraph();
        final EntityId app3Id = Edmm.addComponent(selector2, "app-3", new HashMap<>(), SoftwareComponent.class);
        final EntityId webServerId = Edmm.addComponent(selector2, "web-server", new HashMap<>(), WebServer.class);
        Edmm.addRelation(selector2, app3Id, webServerId, HostedOn.class);
        Edmm.addComponent(selector2, "dbms", new HashMap<>(), MysqlDbms.class);
        final Graph<RootComponent, RootRelation> selectorGraph2 = EdmmGraphCreator.of(selector2);

        result = checker.checkCompliance(rule, selectorGraph2, checkerGraph);
        Assertions.assertEquals(Outcome.NO_VIOLATIONS, result.getOutcome());

    }

}