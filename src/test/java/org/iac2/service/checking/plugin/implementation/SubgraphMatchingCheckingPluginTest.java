package org.iac2.service.checking.plugin.implementation;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Map;

import io.github.edmm.model.Property;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.iac2.common.utility.EdmmTypeResolver;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerContainer;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerEngine;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.SubgraphMatchingCheckingPlugin;
import org.jgrapht.Graph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SubgraphMatchingCheckingPluginTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubgraphMatchingCheckingPluginTest.class);
    private static final String RULE_PATH = "http://localhost:8080/winery/compliancerules/http%253A%252F%252Fwww.example.org%252Ftosca%252Fcompliancerules/no-unexpected-docker-containers_w1-wip1";

    @BeforeEach
    void init() {
        EdmmTypeResolver.putMapping("docker_container", DockerContainer.class);
        EdmmTypeResolver.putMapping("docker_engine", DockerEngine.class);
    }

    @Test
    void getRequiredStructure() throws URISyntaxException, IOException, InterruptedException {
        SubgraphMatchingCheckingPlugin plugin = new SubgraphMatchingCheckingPlugin();
        final String path = String.format("%s/identifier/edmm/export?edmmUseAbsolutePaths=true", RULE_PATH);

        Graph<RootComponent, RootRelation> graph = plugin.getRulePart(path);
        Assertions.assertEquals(4, graph.vertexSet().size());
        Assertions.assertTrue(graph.vertexSet().stream().anyMatch( v -> v instanceof DockerEngine));
        Assertions.assertTrue(graph.vertexSet().stream().anyMatch( v -> v instanceof DockerContainer));
        RootComponent vertex = graph.vertexSet().stream().findFirst().orElseThrow();
        Map<String, Property> properties = vertex.getProperties();
        Assertions.assertTrue(properties.size() > 0);
        StringWriter w = new StringWriter();
        vertex.getEntity().getGraph().generateYamlOutput(w);
        LOGGER.info(w.toString());
    }
}