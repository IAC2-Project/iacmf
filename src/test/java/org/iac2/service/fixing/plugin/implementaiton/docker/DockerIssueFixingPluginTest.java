package org.iac2.service.fixing.plugin.implementaiton.docker;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import io.github.edmm.model.component.RootComponent;
import org.assertj.core.util.Sets;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.utility.Edmm;
import org.iac2.common.utility.EdmmTypeResolver;
import org.iac2.common.utility.Utils;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelRefinementPlugin;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerEngine;
import org.iac2.service.architecturereconstruction.plugin.factory.implementation.SimpleARPluginFactory;
import org.iac2.service.architecturereconstruction.plugin.implementation.opentoscacontainer.DockerContainerRefinementPluginTest;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.SubgraphMatchingCheckingPlugin;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.SubgraphMatchingCheckingPluginDescriptor;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.iac2.service.fixing.common.model.IssueFixingReport;
import org.iac2.service.fixing.plugin.factory.implementation.SimpleIssueFixingPluginFactory;
import org.iac2.util.OpenTOSCATestUtils;
import org.iac2.util.TestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opentosca.container.client.ContainerClient;
import org.opentosca.container.client.ContainerClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DockerIssueFixingPluginTest {

    // TODO we should move all this opentosca startup code into a separate abstract class
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerContainerRefinementPluginTest.class);
    private static final String TESTAPPLICATIONSREPOSITORY = "https://github.com/OpenTOSCA/tosca-definitions-example-applications";
    private static final QName csarId = new QName("http://opentosca.org/example/applications/servicetemplates", "RealWorld-Application_Angular-Spring-MySQL-w1");
    private static final String hostName = "localhost";
    private static final String port = "1337";
    private static final ContainerClient client = ContainerClientBuilder.builder().withHostname(hostName).withPort(Integer.valueOf(port)).withTimeout(20, TimeUnit.MINUTES).build();
    private static final String RULE_PATH = "http://localhost:8080/winery/compliancerules/http%253A%252F%252Fwww.example.org%252Ftosca%252Fcompliancerules/no-unexpected-docker-containers_w1-wip1";
    private static Path csarPath;
    private static String appName = "RealWorld-Application_Angular-Spring-MySQL-w1";
    private static String instanceId = "";
    // set this to true if you want faster execution of this test when you probably need to run it more often
    private static boolean debugging = true;
    private static boolean onlyLocal = false;

    @BeforeAll
    public static void setupContainer() throws GitAPIException, AccountabilityException, RepositoryCorruptException, IOException, ExecutionException, InterruptedException {
        EdmmTypeResolver.initDefaultMappings();
        if (!onlyLocal) {
            csarPath = TestUtils.fetchCsar(TESTAPPLICATIONSREPOSITORY, csarId);
            appName = csarPath.getFileName().toString();
            OpenTOSCATestUtils.uploadApp(client, appName, csarPath);
            instanceId = OpenTOSCATestUtils.provisionApp(client, appName);
        }
    }

    @AfterAll
    public static void cleanupContainer() {
        if (!debugging && !onlyLocal) {
            OpenTOSCATestUtils.terminateApp(client, appName, hostName, port);
            client.getApplications().forEach(a -> client.removeApplication(a));
        }
    }

    private static InstanceModel setupInstance(ProductionSystem productionSystem) {
        ModelCreationPlugin plugin = OpenTOSCATestUtils.getOpenTOSCAModelCreationPlugin();
        InstanceModel instanceModel = plugin.reconstructInstanceModel(productionSystem);
        return instanceModel;
    }

    private static InstanceModel enhanceInstance(ProductionSystem productionSystem, InstanceModel instanceModel) {
        SimpleARPluginFactory instance = SimpleARPluginFactory.getInstance();
        ModelRefinementPlugin enhancementPlugin = instance.createModelRefinementPlugin("docker-refinement-plugin");
        return enhancementPlugin.refineModel(instanceModel, productionSystem);
    }

    private static Collection<DockerEngine> addFaultToInstance(InstanceModel instanceModel) {
        Collection<DockerEngine> dockerEngineComponents =
                Edmm.getAllComponentsOfType(instanceModel.getDeploymentModel(), DockerEngine.class);
        for (RootComponent d : dockerEngineComponents) {
            String dockerEngineUrl = d.getProperty("DockerEngineURL").orElseThrow().getValue();

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
        }

        return dockerEngineComponents;
    }

    private static TestParams setupIssues() {
        ProductionSystem productionSystem = OpenTOSCATestUtils.createProductionSystem(hostName, port, appName, instanceId);
        InstanceModel instanceModel = setupInstance(productionSystem);
        Collection<DockerEngine> dockerEngines = addFaultToInstance(instanceModel);
        instanceModel = enhanceInstance(productionSystem, instanceModel);
        SubgraphMatchingCheckingPlugin checkingPlugin = new SubgraphMatchingCheckingPlugin(new SubgraphMatchingCheckingPluginDescriptor());

        ComplianceRule rule = new ComplianceRule(1L, "subgraph-matching", RULE_PATH,
                DockerContainerIssueFixingPluginDescriptor.SUPPORTED_ISSUE_TYPES[0]);
        rule.addStringParameter("ENGINE_URL", "tcp://" + TestUtils.getDockerHost() + ":2375");
        Collection<ComplianceIssue> issues = checkingPlugin.findIssues(instanceModel, rule);
        Assertions.assertEquals(1, issues.size());
        ComplianceIssue issue = issues.iterator().next();
        TestParams params = new TestParams();
        params.instanceModel = instanceModel;
        params.dockerEngines = dockerEngines;
        params.issue = issue;
        return params;
    }

    private static Collection<String> cleanUpDockerContainersByImage(String dockerEngineUrl) {
        DockerClient dockerClient = Utils.createDockerClient(dockerEngineUrl);
        Collection<String> removedContainerIds = Sets.newHashSet();

        dockerClient.listContainersCmd().exec().stream().filter(c -> c.getImageId().equals("strm/helloworld-http")).forEach(c -> {
            dockerClient.stopContainerCmd(c.getId()).exec();
            dockerClient.removeContainerCmd(c.getId()).exec();
            removedContainerIds.add(c.getId());
        });
        return removedContainerIds;
    }

    @Test
    void getPlugin() {
        SimpleIssueFixingPluginFactory instance = SimpleIssueFixingPluginFactory.getInstance();
        IssueFixingPlugin plugin = instance.createPlugin("docker-container-issue-fixing-plugin");
        assertNotNull(plugin);
        assertThrows(PluginNotFoundException.class, () -> instance.createPlugin("abc"));
    }

    @Test
    void testFixingDockerContainers() {
        TestParams params = setupIssues();

        SimpleIssueFixingPluginFactory instance = SimpleIssueFixingPluginFactory.getInstance();
        ProductionSystem productionSystem = new ProductionSystem(
                "opentoscacontainer",
                "bla bla",
                new HashMap<>());

        Collection<String> plugins = instance.getSuitablePluginIdentifiers(params.issue.getType(), productionSystem);
        assertNotNull(plugins);
        assertTrue(plugins.size() > 0);

        String pluginId = plugins.iterator().next();
        assertNotNull(pluginId);
        assertNotNull(params.issue);

        int compsOrigSize = params.instanceModel.getDeploymentModel().getComponents().size();
        int relationsOrigSize = params.instanceModel.getDeploymentModel().getRelations().size();
        IssueFixingPlugin plugin = instance.createPlugin(pluginId);
        assertNotNull(plugin);
        IssueFixingReport report = plugin.fixIssue(params.issue, params.instanceModel, productionSystem);

        assertNotNull(report);
        assertTrue(report.isSuccessful());

        int compsFixedSize = params.instanceModel.getDeploymentModel().getComponents().size();
        int relationsFixedSize = params.instanceModel.getDeploymentModel().getRelations().size();

        assertNotEquals(compsOrigSize, compsFixedSize);
        assertNotEquals(relationsOrigSize, relationsFixedSize);

        assertEquals(compsFixedSize, compsOrigSize - 1);
        assertEquals(relationsFixedSize, relationsOrigSize - 1);

        Collection<String> danglingContainerIds = Sets.newHashSet();
        params.dockerEngines.forEach(dockerEngine -> {
            String dockerEngineUrl = dockerEngine.getProperty("DockerEngineURL").orElseThrow().getValue();

            if (dockerEngineUrl.contains("host.docker.internal")) {
                // this is a little dirty, as we use such an URL in the test environment,
                // we assume this URL is never like this but only a proper URL/IP
                // => TODO: FIXME
                dockerEngineUrl = dockerEngineUrl.replace("host.docker.internal", "localhost");
            }
            danglingContainerIds.addAll(cleanUpDockerContainersByImage(dockerEngineUrl));
        });

        assertEquals(0, danglingContainerIds.size());
    }

    public static class TestParams {
        public InstanceModel instanceModel;
        public ComplianceIssue issue;
        public Collection<DockerEngine> dockerEngines;
    }
}
