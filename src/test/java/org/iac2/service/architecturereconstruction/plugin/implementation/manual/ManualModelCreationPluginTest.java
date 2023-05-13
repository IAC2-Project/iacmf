package org.iac2.service.architecturereconstruction.plugin.implementation.manual;

import java.util.HashMap;

import org.iac2.common.exception.MissingConfigurationEntryException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManualModelCreationPluginTest {
    private static final String path =
            "http://localhost:8080/winery/servicetemplates/https%253A%252F%252Fedmm.uni-stuttgart.de%252Fservicetemplates/MySQL-OpenStack/edmm/export?edmmUseAbsolutePaths=true";

    @Test
    void testFetchFromWinery() {
        ManualModelCreationPlugin plugin = new ManualModelCreationPlugin(new ManualModelCreationPluginDescriptor());
        plugin.setConfigurationEntry(ManualModelCreationPluginDescriptor.CONFIG_ENTRY_MODEL_PATH, path);
        InstanceModel result = plugin.reconstructInstanceModel(
                new ProductionSystem("dummy", "none of your business", "saaa", new HashMap<>()));
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getDeploymentModel());
        Assertions.assertEquals(3, result.getDeploymentModel().getComponents().size());
    }

    @Test
    void testTheObvious() {
        ManualModelCreationPlugin plugin = new ManualModelCreationPlugin(new ManualModelCreationPluginDescriptor());
        Assertions.assertTrue(((ManualModelCreationPluginDescriptor) plugin.getDescriptor()).isIaCTechnologySupported("opentoscacontainer"));
        Assertions.assertTrue(((ManualModelCreationPluginDescriptor) plugin.getDescriptor()).isIaCTechnologySupported("random tech name"));
        Assertions.assertEquals(0, ((ManualModelCreationPluginDescriptor) plugin.getDescriptor()).getRequiredProductionSystemPropertyNames().size());
        Assertions.assertEquals(1, plugin.getDescriptor().getConfigurationEntryDescriptors().size());
        Assertions.assertThrows(MissingConfigurationEntryException.class, () ->
                plugin.reconstructInstanceModel(new ProductionSystem("dummy", "", "", new HashMap<>())));
        plugin.setConfigurationEntry(ManualModelCreationPluginDescriptor.CONFIG_ENTRY_MODEL_PATH, "random path!");
        Assertions.assertThrows(RuntimeException.class, () ->
                plugin.reconstructInstanceModel(new ProductionSystem("dummy", "", "", new HashMap<>())));
    }

    @Test
    void testSimplePaths() {
        String simplePath = "http://localhost:8081/#/servicetemplates/http%253A%252F%252Fiac2.org%252Fiacmf%252Fservice-templates/simple-instance-model/readme";
        String fullPath = "http://localhost:8081/winery/servicetemplates/http%253A%252F%252Fiac2.org%252Fiacmf%252Fservice-templates/simple-instance-model/edmm/export?edmmUseAbsolutePaths=true";
        ManualModelCreationPlugin plugin = new ManualModelCreationPlugin(new ManualModelCreationPluginDescriptor());
        plugin.setConfigurationEntry(ManualModelCreationPluginDescriptor.CONFIG_ENTRY_MODEL_PATH, simplePath);
        String processedPath = plugin.getConfigurationEntry(ManualModelCreationPluginDescriptor.CONFIG_ENTRY_MODEL_PATH);
        Assertions.assertEquals(fullPath, processedPath);
    }
}