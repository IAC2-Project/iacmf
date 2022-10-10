package org.iac2.util;

import io.swagger.client.model.ServiceTemplateInstanceDTO;
import org.junit.jupiter.api.Assertions;
import org.opentosca.container.client.ContainerClient;
import org.opentosca.container.client.model.Application;
import org.opentosca.container.client.model.ApplicationInstance;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class OpenTOSCATestUtils {

    public static void uploadApp(ContainerClient client, String appName, Path csarPath) {
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

    public static String provisionApp(ContainerClient client, String appName) {
        String instanceId = "";
        Application application = client.getApplication(appName).orElseThrow(IllegalStateException::new);
        Collection<ApplicationInstance> instances = client.getApplicationInstances(application);

        if (instances.isEmpty()) {
            Assertions.assertEquals(0, client.getApplicationInstances(application, ServiceTemplateInstanceDTO.StateEnum.CREATED).size());
            int startSize = client.getApplicationInstances(application).size();
            ApplicationInstance instance = client.provisionApplication(application, TestUtils.getProvisioningInputParameters());
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
        return instanceId;
    }

    public static void terminateApp(ContainerClient client, String appName, String hostName, String port) {
        ApplicationInstance instance = client.getApplicationInstances(client.getApplication(appName).get()).stream().findFirst().get();
        client.terminateApplicationInstance(instance, TestUtils.getTerminationPlanInputParameters(TestUtils.getServiceInstanceURL(hostName, port, appName, instance.getApplication().getServiceTemplate().getId(), instance.getId())));
    }
}
