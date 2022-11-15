package org.iac2.service.architecturereconstruction.plugin.implementation.opentoscacontainer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Container;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.assertj.core.util.Sets;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.utility.Edmm;
import org.iac2.common.utility.Utils;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelEnhancementPlugin;
import org.iac2.service.architecturereconstruction.plugin.manager.implementation.SimpleARPluginManager;
import org.iac2.util.OpenTOSCATestUtils;
import org.iac2.util.TestUtils;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opentosca.container.client.ContainerClient;
import org.opentosca.container.client.ContainerClientBuilder;
import org.springframework.core.io.ClassPathResource;

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
    public void testAddDockerContainerToModel() throws IOException, IllegalAccessException {
        ClassPathResource resource = new ClassPathResource("edmm/four-components-hosted-on.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        ClassPathResource containerInfo = new ClassPathResource("edmm/test-container.json");
        ModelEnhancementPlugin enhancementPlugin = SimpleARPluginManager.getInstance().getModelEnhancementPlugin("docker-enhancement-plugin");
        Container container = new ObjectMapper().readValue(containerInfo.getFile(), Container.class);
        RootComponent engine = model.getComponent("tomcat").get();
        model = ((DockerContainerEnhancementPlugin) enhancementPlugin).addDockerContainerToDeploymentModel(model, engine, container);
        Assertions.assertEquals(5, model.getComponents().size());
    }

    @Test
    public void testDockerReconstruction() {
        ProductionSystem productionSystem = OpenTOSCATestUtils.createProductionSystem(this.hostName, this.port, this.appName, this.instanceId);
        ModelCreationPlugin plugin = OpenTOSCATestUtils.getOpenTOSCAModelCreationPlugin();
        InstanceModel instanceModel = plugin.reconstructInstanceModel(productionSystem);
        Set<RootComponent> comps = instanceModel.getDeploymentModel().getComponents();
        Set<RootRelation> rels = instanceModel.getDeploymentModel().getRelations();

        SimpleARPluginManager instance = SimpleARPluginManager.getInstance();
        ModelEnhancementPlugin enhancementPlugin = instance.getModelEnhancementPlugin("docker-enhancement-plugin");
        InstanceModel instanceModel1 = enhancementPlugin.enhanceModel(instanceModel, productionSystem);

        Set<RootComponent> newComps = instanceModel1.getDeploymentModel().getComponents();
        Set<RootRelation> newRels = instanceModel1.getDeploymentModel().getRelations();

        Assert.assertEquals(comps.size(), newComps.size());
        Assert.assertEquals(rels.size(), newRels.size());

        Collection<RootComponent> dockerContainersBeforeEnhancement = this.getDockerContainers(comps);
        Collection<RootComponent> dockerContainersAfterEnhancement = this.getDockerContainers(newComps);

        Assert.assertEquals(dockerContainersBeforeEnhancement.size(), dockerContainersAfterEnhancement.size());

        // up to this point the enhanced deployment model should be equal to the deployment
        // now we introduce a new docker container per engine therefore try to create compliance issues

        Collection<String> newContainerIds = Sets.newHashSet();
        Collection<RootComponent> dockerEngineComponents = Edmm.getDockerEngineComponents(instanceModel.getDeploymentModel());
        for (RootComponent d : dockerEngineComponents) {
            String dockerEngineUrl = d.getProperty("DockerEngineURL").get().getValue();

            if (dockerEngineUrl.contains("host.docker.internal")) {
                // this is a little dirty, as we use such an URL in the test environment,
                // we assume this URL is never like this but only a proper URL/IP
                // => TODO: FIXME
                dockerEngineUrl = dockerEngineUrl.replace("host.docker.internal", "localhost");
            }

            DockerClient dockerClient = Utils.createDockerClient(dockerEngineUrl);

            try {
                dockerClient.pullImageCmd("strm/helloworld-http")
                        .exec(new PullImageResultCallback())
                        .awaitCompletion();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            CreateContainerResponse newContainer = dockerClient.createContainerCmd("strm/helloworld-http").exec();
            dockerClient.startContainerCmd(newContainer.getId()).exec();
            newContainerIds.add(newContainer.getId());
        }

        // now the enhanced model should not be the model given by opentosca
        instanceModel1 = enhancementPlugin.enhanceModel(instanceModel, productionSystem);

        newComps = instanceModel1.getDeploymentModel().getComponents();
        newRels = instanceModel1.getDeploymentModel().getRelations();

        Assert.assertNotEquals(comps.size(), newComps.size());
        Assert.assertNotEquals(rels.size(), newRels.size());

        dockerContainersBeforeEnhancement = this.getDockerContainers(comps);
        dockerContainersAfterEnhancement = this.getDockerContainers(newComps);

        Assert.assertNotEquals(dockerContainersBeforeEnhancement.size(), dockerContainersAfterEnhancement.size());
    }

    private Collection<RootComponent> getDockerContainers(Collection<RootComponent> comps) {
        return comps
                .stream()
                .filter(c -> 
                        c.getProperties()
                        .values()
                        .stream()
                        .anyMatch(p -> p.getName().equals("container_id")))
                .collect(Collectors.toList());
    }
}
