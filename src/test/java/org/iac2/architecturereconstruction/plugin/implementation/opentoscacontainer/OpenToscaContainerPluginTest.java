package org.iac2.architecturereconstruction.plugin.implementation.opentoscacontainer;

import com.google.common.collect.Maps;
import io.swagger.client.model.ServiceTemplateInstanceDTO;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.winery.accountability.exceptions.AccountabilityException;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;
import org.iac2.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.architecturereconstruction.plugin.manager.implementations.SimpleARPluginManager;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.SystemModel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opentosca.container.client.ContainerClient;
import org.opentosca.container.client.ContainerClientBuilder;
import org.opentosca.container.client.model.Application;
import org.opentosca.container.client.model.ApplicationInstance;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class OpenToscaContainerPluginTest {

    private static final TestUtils testUtils = new TestUtils();

    private static final String TESTAPPLICATIONSREPOSITORY = "https://github.com/OpenTOSCA/tosca-definitions-example-applications";
    private static final QName csarId = new QName("http://opentosca.org/example/applications/servicetemplates", "RealWorld-Application_Angular-Spring-MySQL-w1");
    private static Path csarPath;
    private static String appName = "RealWorld-Application_Angular-Spring-MySQL-w1";;

    private static final String hostName = "localhost";
    private static final String port = "1337";
    private static String instanceId = "";
    private static ContainerClient client = ContainerClientBuilder.builder().withHostname(hostName).withPort(Integer.valueOf(port)).build();

    // set this to true if you want faster execution of this test when you probably need to run it more often
    private static boolean debugging = false;


    @BeforeAll
    public static void setupContainer() throws GitAPIException, AccountabilityException, RepositoryCorruptException, IOException, ExecutionException, InterruptedException {
        csarPath = testUtils.fetchCsar(TESTAPPLICATIONSREPOSITORY, csarId);
        appName = csarPath.getFileName().toString();
        uploadApp();
        provisionApp();
    }

    @AfterAll
    public static void cleanupContainer() {
        if (!debugging) {
            terminateApp();
            client.getApplications().forEach(a -> client.removeApplication(a));
        }
    }

    @Test
    public void testReconstruction() {
        SimpleARPluginManager instance = SimpleARPluginManager.getInstance();
        ModelCreationPlugin plugin = instance.getModelCreationPlugin("opentoscacontainerplugin");
        assertNotNull(plugin);
        assertEquals("opentoscacontainerplugin", plugin.getIdentifier());

        Map<String, String> prodProps = Maps.newHashMap();
        prodProps.put("opentoscacontainer_hostname", hostName);
        prodProps.put("opentoscacontainer_port", port);
        prodProps.put("opentoscacontainer_appId", appName);
        prodProps.put("opentoscacontainer_instanceId", instanceId);
        ProductionSystem productionSystem = new ProductionSystem("opentoscacontainer", "realworldapp-test", prodProps);

        SystemModel systemModel = plugin.reconstructInstanceModel(productionSystem);
    }

    private static void uploadApp() {
        List<Application> applications = client.getApplications();
        Application application = null;
        for (Application app : applications) {
            if (app.getId().equals(appName)) {
                application = app;
            }
        }

        if (application == null) {
            application = client.uploadApplication(csarPath);
        }

        Assertions.assertNotNull(application);
        Assertions.assertEquals(csarPath.getFileName().toString(), application.getId());
        Assertions.assertEquals(1, client.getApplications().size());
    }

    private static void provisionApp() {
        Application application = client.getApplication(appName).orElseThrow(IllegalStateException::new);
        Collection<ApplicationInstance> instances = client.getApplicationInstances(application);

        if (instances.isEmpty()) {
            Assertions.assertEquals(0, client.getApplicationInstances(application, ServiceTemplateInstanceDTO.StateEnum.CREATED).size());
            int startSize = client.getApplicationInstances(application).size();
            ApplicationInstance instance = client.provisionApplication(application, testUtils.getProvisioningInputParameters());
            Assertions.assertNotNull(instance);
            Assertions.assertEquals(ServiceTemplateInstanceDTO.StateEnum.CREATED, instance.getState());
            instances = client.getApplicationInstances(application);
            instanceId = instances.stream().map(i -> i.getId()).findFirst().orElse(null);
            Assertions.assertEquals(startSize + 1, instances.size());
            Assertions.assertEquals(1, client.getApplicationInstances(application, ServiceTemplateInstanceDTO.StateEnum.CREATED).size());
        } else {
            instances = client.getApplicationInstances(application);
            instanceId = instances.stream().map(i -> i.getId()).findFirst().orElse(null);
        }

        Assertions.assertNotNull(instanceId);
    }

    private static void terminateApp() {
        ApplicationInstance instance = client.getApplicationInstances(client.getApplication(appName).get()).stream().findFirst().get();
        client.terminateApplicationInstance(instance, testUtils.getTerminationPlanInputParameters(testUtils.getServiceInstanceURL(hostName,port,appName,instance.getApplication().getServiceTemplate().getId(),instance.getId())));
    }
}
