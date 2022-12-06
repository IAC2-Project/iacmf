package org.iac2.service.checking.plugin.implementation.subgraphmatching;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.Go;
import io.github.edmm.model.component.MysqlDatabase;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.component.Tomcat;
import io.github.edmm.model.component.WebApplication;
import io.github.edmm.model.component.WebServer;
import io.github.edmm.model.relation.HostedOn;
import io.github.edmm.model.relation.RootRelation;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.utility.Edmm;
import org.iac2.common.utility.EdmmTypeResolver;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerContainer;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerEngine;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.MySqlDbms;
import org.iac2.service.checking.common.interfaces.RuleValidationResult;
import org.jgrapht.Graph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class RuleCheckerTest {

    @BeforeAll
    static void init() {
        EdmmTypeResolver.initDefaultMappings();
    }

    @Test
    void testInvalidRule() throws IllegalAccessException {
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

        final ComplianceRule rule = new ComplianceRule(1L, "subgraph-matching", "somewhere");

        RuleChecker checker = new RuleChecker(instanceModel);
        RuleCheckingResult result = checker.checkCompliance(rule, selectorGraph, checkerGraph);
        Assertions.assertEquals(RuleCheckingOutcome.INVALID_RULE, result.getOutcome());

        final EntityGraph selector2 = new EntityGraph();
        final EntityId app3Id = Edmm.addComponent(selector2, "app-3", new HashMap<>(), SoftwareComponent.class);
        final EntityId webServerId = Edmm.addComponent(selector2, "web-server", new HashMap<>(), WebServer.class);
        Edmm.addRelation(selector2, app3Id, webServerId, HostedOn.class);
        Edmm.addComponent(selector2, "dbms", new HashMap<>(), MySqlDbms.class);
        Edmm.addPropertyExpressionAssignment(selector2, app3Id, "a", "string", "value.length()");
        final Graph<RootComponent, RootRelation> selectorGraph2 = EdmmGraphCreator.of(selector2);

        result = checker.checkCompliance(rule, selectorGraph2, checkerGraph);
        Assertions.assertEquals(RuleCheckingOutcome.INVALID_RULE, result.getOutcome());

        final EntityGraph selector3 = new EntityGraph();
        final EntityId app4Id = Edmm.addComponent(selector3, "app-4", new HashMap<>(), SoftwareComponent.class);
        final EntityId webServer2Id = Edmm.addComponent(selector3, "web-server-2", new HashMap<>(), WebServer.class);
        Edmm.addRelation(selector3, app4Id, webServer2Id, HostedOn.class);
        Edmm.addComponent(selector3, "dbms", new HashMap<>(), MySqlDbms.class);
        Edmm.addPropertyExpressionAssignment(selector3, app4Id, "a", "string", "value.length() > 0");
        final Graph<RootComponent, RootRelation> selectorGraph3 = EdmmGraphCreator.of(selector3);

        result = checker.checkCompliance(rule, selectorGraph3, checkerGraph);
        Assertions.assertEquals(RuleCheckingOutcome.NO_VIOLATIONS, result.getOutcome());
    }

    @Test
    void testRegularOperation() throws IOException, IllegalAccessException {
        ClassPathResource resource = new ClassPathResource("edmm/cr1-selector.yaml");
        DeploymentModel selectorDM = DeploymentModel.of(resource.getFile());
        resource = new ClassPathResource("edmm/cr1-checker.yaml");
        DeploymentModel checkerDM = DeploymentModel.of(resource.getFile());

        // create an instance model
        EntityGraph instanceGraph = new EntityGraph();
        EntityId dockerEngineId = Edmm.addComponent(
                instanceGraph,
                "engine-1",
                Map.of("DockerEngineUrl", "https://localhost:1234"),
                DockerEngine.class
        );
        EntityId dockerContainerId = Edmm.addComponent(
                instanceGraph,
                "component-1",
                Map.of(
                        "DockerImage", "ubuntu",
                        "structuralState", "ExpEcTed"
                ),
                DockerContainer.class
        );
        EntityId appId = Edmm.addComponent(
                instanceGraph,
                "app-1",
                new HashMap<>(),
                WebApplication.class
        );
        Edmm.addRelation(instanceGraph, dockerContainerId, dockerEngineId, HostedOn.class);
        Edmm.addRelation(instanceGraph, appId, dockerContainerId, HostedOn.class);
        InstanceModel instanceModel = new InstanceModel(new DeploymentModel("instance-model", instanceGraph));

        // create a compliance rule
        ComplianceRule complianceRule = new ComplianceRule(1L, "subgraph-matching", "nowhere");
        complianceRule.addStringParameter("ENGINE_URL", "https://localhost:1234");

        Graph<RootComponent, RootRelation> selectorGraph = EdmmGraphCreator.of(selectorDM);
        Graph<RootComponent, RootRelation> checkerGraph = EdmmGraphCreator.of(checkerDM);
        RuleValidationResult validationResult = RuleValidator.validateComplianceRule(complianceRule, selectorGraph, checkerGraph);
        Assertions.assertTrue(validationResult.isValid());

        RuleChecker ruleChecker = new RuleChecker(instanceModel);
        RuleCheckingResult result = ruleChecker.checkCompliance(complianceRule, selectorGraph, checkerGraph);
        Assertions.assertEquals(RuleCheckingOutcome.NO_VIOLATIONS, result.getOutcome());

        EntityId newContainerId = Edmm.addComponent(instanceGraph,
                "container-2",
                Map.of("structural_status", "UNEXPECTED"),
                DockerContainer.class);
        Edmm.addRelation(instanceGraph, newContainerId, dockerEngineId, HostedOn.class);
        instanceModel = new InstanceModel(new DeploymentModel("instance-model", instanceGraph));
        ruleChecker = new RuleChecker(instanceModel);
        result = ruleChecker.checkCompliance(complianceRule, selectorGraph, checkerGraph);
        Assertions.assertEquals(RuleCheckingOutcome.COMPLIANCE_VIOLATION, result.getOutcome());
    }

    @Test
    void testRegularOperation2() throws IOException, IllegalAccessException {
        ClassPathResource resource = new ClassPathResource("edmm/cr2-selector.yaml");
        DeploymentModel selectorDM = DeploymentModel.of(resource.getFile());
        resource = new ClassPathResource("edmm/cr2-checker.yaml");
        DeploymentModel checkerDM = DeploymentModel.of(resource.getFile());

        // create an instance model
        EntityGraph instanceGraph = new EntityGraph();
        EntityId dockerEngineId = Edmm.addComponent(
                instanceGraph,
                "engine-1",
                Map.of("DockerEngineUrl", "https://localhost:1234"),
                DockerEngine.class
        );
        EntityId dockerContainerId = Edmm.addComponent(
                instanceGraph,
                "component-1",
                Map.of(
                        "DockerImage", "ubuntu",
                        "structuralState", "ExpEcTed"
                ),
                DockerContainer.class
        );
        EntityId dbmsId = Edmm.addComponent(
                instanceGraph,
                "dbms",
                Map.of("componentName", "production"),
                MySqlDbms.class
        );
        EntityId databaseId = Edmm.addComponent(
                instanceGraph,
                "database1",
                Map.of("user", "C,B"),
                MysqlDatabase.class
        );
        Edmm.addRelation(instanceGraph, dockerContainerId, dockerEngineId, HostedOn.class);
        Edmm.addRelation(instanceGraph, dbmsId, dockerContainerId, HostedOn.class);
        Edmm.addRelation(instanceGraph, databaseId, dbmsId, HostedOn.class);
        InstanceModel instanceModel = new InstanceModel(new DeploymentModel("instance-model", instanceGraph));

        // create a compliance rule
        ComplianceRule complianceRule = new ComplianceRule(1L, "subgraph-matching", "nowhere");
        complianceRule.addStringParameter("DBMS_NAME", "production");
        complianceRule.addStringCollectionParameter("ALLOWED_USERS", List.of("C", "B", "A"));

        Graph<RootComponent, RootRelation> selectorGraph = EdmmGraphCreator.of(selectorDM);
        Graph<RootComponent, RootRelation> checkerGraph = EdmmGraphCreator.of(checkerDM);
        RuleValidationResult validationResult = RuleValidator.validateComplianceRule(complianceRule, selectorGraph, checkerGraph);
        Assertions.assertTrue(validationResult.isValid());

        RuleChecker ruleChecker = new RuleChecker(instanceModel);
        RuleCheckingResult result = ruleChecker.checkCompliance(complianceRule, selectorGraph, checkerGraph);
        Assertions.assertEquals(RuleCheckingOutcome.NO_VIOLATIONS, result.getOutcome());

        EntityId newDb = Edmm.addComponent(instanceGraph,
                "db-2",
                Map.of("user", "C,X,B"),
                MysqlDatabase.class);
        Edmm.addRelation(instanceGraph, newDb, dbmsId, HostedOn.class);
        instanceModel = new InstanceModel(new DeploymentModel("instance-model", instanceGraph));
        ruleChecker = new RuleChecker(instanceModel);
        result = ruleChecker.checkCompliance(complianceRule, selectorGraph, checkerGraph);
        Assertions.assertEquals(RuleCheckingOutcome.COMPLIANCE_VIOLATION, result.getOutcome());
    }
}