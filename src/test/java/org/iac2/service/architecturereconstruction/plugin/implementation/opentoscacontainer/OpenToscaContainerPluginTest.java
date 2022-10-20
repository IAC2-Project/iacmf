package org.iac2.service.architecturereconstruction.plugin.implementation.opentoscacontainer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;

import com.google.common.collect.Maps;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import io.swagger.client.model.ServiceTemplateInstanceDTO;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.plugin.manager.implementation.SimpleARPluginManager;
import org.iac2.util.OpenTOSCATestUtils;
import org.iac2.util.TestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opentosca.container.client.ContainerClient;
import org.opentosca.container.client.ContainerClientBuilder;
import org.opentosca.container.client.model.Application;
import org.opentosca.container.client.model.ApplicationInstance;
import org.opentosca.container.client.model.NodeInstance;
import org.opentosca.container.client.model.RelationInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class OpenToscaContainerPluginTest {

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
        Set<RootComponent> comps = instanceModel.getDeploymentModel().getComponents();
        Set<RootRelation> rels = instanceModel.getDeploymentModel().getRelations();

        ApplicationInstance applicationInstance = this.getInstance();

        assertEquals(applicationInstance.getNodeInstances().size(), comps.size());
        assertEquals(applicationInstance.getRelationInstances().size(), rels.size());

        assertEquals(applicationInstance.getNodeInstances().size(), comps.stream().filter(c -> {
            boolean nodeExists = false;
            boolean relationsExist = true;
            for (NodeInstance nodeInstance : applicationInstance.getNodeInstances()) {
                if (nodeInstance.getId().equals(c.getId())) {
                    nodeExists = true;
                }
            }
            relationsExist = applicationInstance.getRelationInstances().stream().filter(r -> r.getSourceId().equals(c.getId())).collect(Collectors.toList()).size() == c.getRelations().size();
            return nodeExists & relationsExist;
        }).collect(Collectors.toList()).size());
        assertEquals(applicationInstance.getRelationInstances().size(), rels.stream().filter(r -> {
            for (RelationInstance relationInstance : applicationInstance.getRelationInstances()) {
                if (relationInstance.getTargetId().equals(r.getTarget())) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList()).size());

        applicationInstance.getNodeInstances().forEach(n -> {
            Collection<RootComponent> components = instanceModel.getDeploymentModel().getComponents().stream().filter(c ->
                    c.getId().equals(n.getId())).collect(Collectors.toList());
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
}
