package org.iac2.service.fixing.plugin.manager.implementation;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.scaleset.cfbuilder.ec2.Instance;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.RootRelation;
import org.assertj.core.util.Sets;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.common.utility.Edmm;
import org.iac2.common.utility.EdmmTypeResolver;
import org.iac2.common.utility.Utils;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelEnhancementPlugin;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerContainer;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerEngine;
import org.iac2.service.architecturereconstruction.plugin.implementation.opentoscacontainer.DockerContainerEnhancementPluginTest;
import org.iac2.service.architecturereconstruction.plugin.manager.implementation.SimpleARPluginManager;
import org.iac2.service.checking.plugin.implementation.subgraphmatching.SubgraphMatchingCheckingPlugin;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.iac2.service.fixing.common.model.IssueFixingReport;
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

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DockerIssueFixingPluginManagerTest {

    // TODO we should move all this opentosca startup code into a separate abstract class
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerContainerEnhancementPluginTest.class);
    private static final String TESTAPPLICATIONSREPOSITORY = "https://github.com/OpenTOSCA/tosca-definitions-example-applications";
    private static final QName csarId = new QName("http://opentosca.org/example/applications/servicetemplates", "RealWorld-Application_Angular-Spring-MySQL-w1");
    private static final String hostName = "localhost";
    private static final String port = "1337";
    private static Path csarPath;
    private static String appName = "RealWorld-Application_Angular-Spring-MySQL-w1";
    private static String instanceId = "";
    private static final ContainerClient client = ContainerClientBuilder.builder().withHostname(hostName).withPort(Integer.valueOf(port)).withTimeout(20, TimeUnit.MINUTES).build();

    // set this to true if you want faster execution of this test when you probably need to run it more often
    private static boolean debugging = true;

    private static boolean onlyLocal = false;

    private static ProductionSystem productionSystem;
    private static InstanceModel instanceModel;

    private static ComplianceIssue issue;

    private static final String RULE_PATH = "http://localhost:8080/winery/compliancerules/http%253A%252F%252Fwww.example.org%252Ftosca%252Fcompliancerules/no-unexpected-docker-containers_w1-wip1";


    @BeforeAll
    public static void setupContainer() throws GitAPIException, AccountabilityException, RepositoryCorruptException, IOException, ExecutionException, InterruptedException {
        EdmmTypeResolver.initDefaultMappings();
        if (!onlyLocal) {
            csarPath = TestUtils.fetchCsar(TESTAPPLICATIONSREPOSITORY, csarId);
            appName = csarPath.getFileName().toString();
            OpenTOSCATestUtils.uploadApp(client, appName, csarPath);
            instanceId = OpenTOSCATestUtils.provisionApp(client, appName);
        }

        setupIssues();
    }

    @AfterAll
    public static void cleanupContainer() {
        if (!debugging && !onlyLocal) {
            OpenTOSCATestUtils.terminateApp(client, appName, hostName, port);
            client.getApplications().forEach(a -> client.removeApplication(a));
        }
    }

    @Test
    void getPlugin() {
        SimpleIssueFixingPluginManager instance = SimpleIssueFixingPluginManager.getInstance();
        IssueFixingPlugin plugin = instance.getPlugin("docker-container-issue-fixing-plugin");
        assertNotNull(plugin);
        assertThrows(PluginNotFoundException.class, () ->instance.getPlugin("abc"));
    }

    @Test
    void testFixingDockerContainers() {
        SimpleIssueFixingPluginManager instance = SimpleIssueFixingPluginManager.getInstance();
        ProductionSystem productionSystem = new ProductionSystem(
                "opentoscacontainer",
                "bla bla",
                new HashMap<>());

        Collection<IssueFixingPlugin> plugins = instance.getSuitablePlugins(issue, productionSystem);
        assertNotNull(plugins);
        assertEquals(1, plugins.size());

        IssueFixingPlugin plugin = plugins.iterator().next();
        assertNotNull(plugin);
        assertNotNull(issue);

        IssueFixingReport report = plugin.fixIssue(issue, instanceModel, productionSystem);

        assertNotNull(report);
        assertTrue(report.isSuccessful());

    }

    static void setupIssues() {
        productionSystem = OpenTOSCATestUtils.createProductionSystem(hostName, port, appName, instanceId);
        ModelCreationPlugin plugin = OpenTOSCATestUtils.getOpenTOSCAModelCreationPlugin();
        instanceModel = plugin.reconstructInstanceModel(productionSystem);
        Set<RootComponent> comps = instanceModel.getDeploymentModel().getComponents();
        Set<RootRelation> rels = instanceModel.getDeploymentModel().getRelations();

        SimpleARPluginManager instance = SimpleARPluginManager.getInstance();
        ModelEnhancementPlugin enhancementPlugin = instance.getModelEnhancementPlugin("docker-enhancement-plugin");
        InstanceModel instanceModel1 = enhancementPlugin.enhanceModel(instanceModel, productionSystem);

        Set<RootComponent> newComps = instanceModel1.getDeploymentModel().getComponents();
        Set<RootRelation> newRels = instanceModel1.getDeploymentModel().getRelations();

        StringWriter writer1 = new StringWriter();
        instanceModel.getDeploymentModel().getGraph().generateYamlOutput(writer1);
        LOGGER.info(writer1.toString());

        Collection<RootComponent> dockerContainersBeforeEnhancement = getDockerContainers(comps);
        Collection<RootComponent> dockerContainersAfterEnhancement = getDockerContainers(newComps);

        // up to this point the enhanced deployment model should be equal to the deployment
        // now we introduce a new docker container per engine therefore try to create compliance issues

        Collection<String> newContainerIds = Sets.newHashSet();
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
            newContainerIds.add(newContainer.getId());
        }

        // now the enhanced model should not be the model given by opentosca
        instanceModel = enhancementPlugin.enhanceModel(instanceModel, productionSystem);

        SubgraphMatchingCheckingPlugin checkingPlugin = new SubgraphMatchingCheckingPlugin();

        ComplianceRule rule = new ComplianceRule(1L, "subgraph-matching", RULE_PATH);
        rule.addStringParameter("ENGINE_URL", "tcp://host.docker.internal:2375");
        Collection<ComplianceIssue> issues = checkingPlugin.findIssues(instanceModel, rule);
        Assertions.assertEquals(1, issues.size());
        issue = issues.iterator().next();
    }

    private static Collection<RootComponent> getDockerContainers(Collection<RootComponent> comps) {
        return comps
                .stream()
                .filter(c -> c instanceof DockerContainer)
                .collect(Collectors.toList());
    }
}
