package org.iac2.service.architecturereconstruction.plugin.implementation.opentoscacontainer;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;

import com.google.common.collect.Maps;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.util.OpenTOSCATestUtils;
import org.iac2.util.TestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opentosca.container.client.ContainerClient;
import org.opentosca.container.client.ContainerClientBuilder;
import org.opentosca.container.client.model.ApplicationInstance;
import org.opentosca.container.client.model.NodeInstance;
import org.opentosca.container.client.model.RelationInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OpenToscaContainerPluginTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenToscaContainerPluginTest.class);
    private static final String TESTAPPLICATIONSREPOSITORY = "https://github.com/OpenTOSCA/tosca-definitions-example-applications";
    private static final QName csarId = new QName("http://opentosca.org/example/applications/servicetemplates", "RealWorld-Application_Angular-Spring-MySQL-w1");
    private static final String hostName = "localhost";
    private static final String port = "1337";
    private static Path csarPath;
    private static String appName = "RealWorld-Application_Angular-Spring-MySQL-w1";
    private static String instanceId = "";
    private static ContainerClient client = ContainerClientBuilder.builder().withHostname(hostName).withPort(Integer.valueOf(port)).withTimeout(20, TimeUnit.MINUTES).build();

    // set this to true if you want faster execution of this test when you probably need to run it more often
    private static boolean debugging = true;

    @BeforeAll
    public static void setupContainer() throws GitAPIException, AccountabilityException, RepositoryCorruptException, IOException, ExecutionException, InterruptedException {
        csarPath = TestUtils.fetchCsar(TESTAPPLICATIONSREPOSITORY, csarId);
        appName = csarPath.getFileName().toString();
        OpenTOSCATestUtils.uploadApp(client, appName, csarPath);
        instanceId = OpenTOSCATestUtils.provisionApp(client, appName);
    }

    @AfterAll
    public static void cleanupContainer() {
        if (!debugging) {
            OpenTOSCATestUtils.terminateApp(client, appName, hostName, port);
            client.getApplications().forEach(a -> client.removeApplication(a));
        }
    }

    @Test
    public void testReconstruction() {
        ModelCreationPlugin plugin = OpenTOSCATestUtils.getOpenTOSCAModelCreationPlugin();
        assertNotNull(plugin);
        assertEquals("opentosca-container-model-creation-plugin", plugin.getIdentifier());

        Map<String, String> prodProps = Maps.newHashMap();
        prodProps.put("opentoscacontainer_hostname", hostName);
        prodProps.put("opentoscacontainer_port", port);
        prodProps.put("opentoscacontainer_appId", appName);
        prodProps.put("opentoscacontainer_instanceId", instanceId);
        ProductionSystem productionSystem = new ProductionSystem("opentoscacontainer", "realworldapp-test", prodProps);

        InstanceModel instanceModel = plugin.reconstructInstanceModel(productionSystem);
        LOGGER.info("Reconstructed edmm instance model:");
        StringWriter writer = new StringWriter();
        instanceModel.getDeploymentModel().getGraph().generateYamlOutput(writer);
        LOGGER.info(writer.toString());

        Set<RootComponent> comps = instanceModel.getDeploymentModel().getComponents();
        Set<RootRelation> rels = instanceModel.getDeploymentModel().getRelations();

        ApplicationInstance applicationInstance = this.getInstance();

        assertEquals(applicationInstance.getNodeInstances().size(), comps.size());
        assertEquals(applicationInstance.getRelationInstances().size(), rels.size());

        // ensure all edmm components have corresponding node instances
        assertEquals(applicationInstance.getNodeInstances().size(), comps
                .stream()
                .filter(c -> applicationInstance.getNodeInstances()
                        .stream()
                        .anyMatch(i -> i.getTemplate().equals(c.getName())))
                .count());

        // ensure all edmm components have correct outgoing relations
        assertEquals(applicationInstance.getNodeInstances().size(), comps
                .stream()
                .filter(c -> applicationInstance.getRelationInstances()
                        .stream()
                        // find the node instance that corresponds to the source of the current relation
                        // then compare its template id with the name of the current edmm component
                        .filter(r -> findNodeInstanceById(r.getSourceId(), applicationInstance)
                                .getTemplate()
                                .equals(c.getName()))
                        .count() == c.getRelations().size())
                .count());

        assertEquals(applicationInstance.getRelationInstances().size(), rels.stream().filter(r -> {
            for (RelationInstance relationInstance : applicationInstance.getRelationInstances()) {
                if (findNodeInstanceById(r.getTarget(), applicationInstance).getTemplate().equals(r.getTarget())) {
                    return true;
                }
            }
            return false;
        }).count());

        applicationInstance.getNodeInstances().forEach(n -> {
            Collection<RootComponent> components = instanceModel.getDeploymentModel().getComponents()
                    .stream()
                    .filter(c ->
                            c.getName().equals(n.getTemplate()))
                    .toList();
            assertEquals(1, components.size());

            components.forEach(c -> {
                n.getProperties().forEach((k, v) -> {
                    assertTrue(c.getProperties().containsKey(k));
                    assertEquals(c.getProperties().get(k).getValue(), v);
                });
            });
        });
    }

    private ApplicationInstance getInstance() {
        return client.getApplicationInstances(client.getApplication(appName).get()).get(0);
    }

    private static NodeInstance findNodeInstanceById(String id, ApplicationInstance applicationInstance) {
        return applicationInstance.getNodeInstances()
                .stream()
                .filter(ni -> ni.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }
}
