package org.iac2.service.architecturereconstruction.plugin.implementation.opentoscacontainer;

import com.google.common.collect.Maps;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelEnhancementPlugin;
import org.iac2.service.architecturereconstruction.plugin.manager.implementation.SimpleARPluginManager;
import org.iac2.util.OpenTOSCATestUtils;
import org.iac2.util.TestUtils;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opentosca.container.client.ContainerClient;
import org.opentosca.container.client.ContainerClientBuilder;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DockerContainerEnhancementPluginTest {

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
    public void testDockerReconstruction() {
        SimpleARPluginManager instance = SimpleARPluginManager.getInstance();
        ModelCreationPlugin plugin = instance.getModelCreationPlugin("opentosca-container-model-creation-plugin");
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

        ModelEnhancementPlugin enhancementPlugin = instance.getModelEnhancementPlugin("docker-enhancement-plugin");
        InstanceModel instanceModel1 = enhancementPlugin.enhanceModel(instanceModel, productionSystem);

        Set<RootComponent> newComps = instanceModel1.getDeploymentModel().getComponents();
        Set<RootRelation> newRels = instanceModel1.getDeploymentModel().getRelations();

        Assert.assertNotEquals(comps.size(), newComps.size());
        Assert.assertNotEquals(rels.size(), newRels.size());

        Collection<RootComponent> dockerContainersBeforeEnhancement = this.getDockerContainers(comps);
        Collection<RootComponent> dockerContainersAfterEnhancement = this.getDockerContainers(newComps);

        Assert.assertNotEquals(dockerContainersBeforeEnhancement.size(), dockerContainersAfterEnhancement.size());
    }

    private Collection<RootComponent> getDockerContainers(Collection<RootComponent> comps) {
        return comps.stream().filter(c -> c.getProperties().values().stream().filter(p -> p.getName().equals("ContainerID")).findFirst().isPresent()).collect(Collectors.toList());
    }
}
