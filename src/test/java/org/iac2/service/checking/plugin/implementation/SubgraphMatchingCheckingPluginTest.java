package org.iac2.service.checking.plugin.implementation;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.utility.EdmmTypeResolver;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerContainer;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerEngine;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.SubgraphMatchingCheckingPlugin;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.SubgraphMatchingCheckingPluginDescriptor;
import org.jgrapht.Graph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

class SubgraphMatchingCheckingPluginTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubgraphMatchingCheckingPluginTest.class);
    private static final String RULE_PATH = "http://localhost:8080/winery/compliancerules/http%253A%252F%252Fwww.example.org%252Ftosca%252Fcompliancerules/no-unexpected-docker-containers_w1-wip1";

    @BeforeAll
    static void init() {
        EdmmTypeResolver.initDefaultMappings();
    }

    @Test
    void getRequiredStructure() throws URISyntaxException, IOException, InterruptedException {
        SubgraphMatchingCheckingPlugin plugin = new SubgraphMatchingCheckingPlugin(new SubgraphMatchingCheckingPluginDescriptor());
        final String path = String.format("%s/identifier/edmm/export?edmmUseAbsolutePaths=true", RULE_PATH);

        Graph<RootComponent, RootRelation> graph = plugin.getRulePart(path);
        Assertions.assertEquals(2, graph.vertexSet().size());
        Assertions.assertTrue(graph.vertexSet().stream().anyMatch(v -> v instanceof DockerEngine));
        Assertions.assertTrue(graph.vertexSet().stream().anyMatch(v -> v instanceof DockerContainer));
        RootComponent vertex = graph.vertexSet().stream().findFirst().orElseThrow();
        Map<String, Property> properties = vertex.getProperties();
        Assertions.assertTrue(properties.size() > 0);
        StringWriter w = new StringWriter();
        vertex.getEntity().getGraph().generateYamlOutput(w);
        LOGGER.debug(w.toString());
    }

    @Test
    void testRegularOperation() throws IOException {
        SubgraphMatchingCheckingPlugin plugin = new SubgraphMatchingCheckingPlugin(new SubgraphMatchingCheckingPluginDescriptor());
        ClassPathResource resource = new ClassPathResource("edmm/realworld_application_instance_model_docker_refined.yaml");
        InstanceModel instanceModel = new InstanceModel(DeploymentModel.of(resource.getFile()));
        ComplianceRule rule = new ComplianceRule(1L, "subgraph-matching", RULE_PATH);
        rule.addStringParameter("ENGINE_URL", "tcp://172.17.0.1:2375");
        Collection<ComplianceIssue> issues = plugin.findIssues(instanceModel, rule);
        Assertions.assertEquals(0, issues.size());
    }
}
