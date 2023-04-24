package org.iac2.service.architecturereconstruction.plugin.implementation.kubernetes;

import io.kubernetes.client.ApiException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.util.KubernetesTestUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
public class KubernetesModelCreationPluginTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesModelCreationPluginTest.class);

    private static final String kubeConfigPath = "D:\\UNI_Courses\\Research_Project\\config";

    private static final String namespace = "pet-clinic";

    @Test
    public void testReconstruction() throws FileNotFoundException, ApiException {
        ModelCreationPlugin plugin = KubernetesTestUtil.getKubernetesModelCreationPlugin();
        assertNotNull(plugin);
        assertEquals("kubernetes-model-creation-plugin", plugin.getIdentifier());

        ProductionSystem productionSystem = KubernetesTestUtil.createProductionSystem(kubeConfigPath,namespace);

        InstanceModel instanceModel = plugin.reconstructInstanceModel(productionSystem);
        LOGGER.info("Reconstructed edmm instance model:");
        StringWriter writer = new StringWriter();
        instanceModel.getDeploymentModel().getGraph().generateYamlOutput(writer);
        LOGGER.info(writer.toString());

    }

}
