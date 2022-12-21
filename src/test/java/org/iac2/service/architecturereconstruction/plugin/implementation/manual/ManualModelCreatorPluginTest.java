package org.iac2.service.architecturereconstruction.plugin.implementation.manual;

import java.util.HashMap;

import org.iac2.common.exception.ConfigurationMissingException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManualModelCreatorPluginTest {
    private static final String path =
            "http://localhost:8080/winery/servicetemplates/https%253A%252F%252Fedmm.uni-stuttgart.de%252Fservicetemplates/MySQL-OpenStack/edmm/export?edmmUseAbsolutePaths";

    @Test
    void testFetchFromWinery() {
        ManualModelCreatorPlugin plugin = new ManualModelCreatorPlugin();
        plugin.setConfigurationEntry(ManualModelCreatorPlugin.CONFIG_ENTRY_MODEL_PATH, path);
        InstanceModel result = plugin.reconstructInstanceModel(
                new ProductionSystem("none of your business", "saaa", new HashMap<>()));
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getDeploymentModel());
        Assertions.assertEquals(3, result.getDeploymentModel().getComponents().size());
    }

    @Test
    void testTheObvious() {
        ManualModelCreatorPlugin plugin = new ManualModelCreatorPlugin();
        Assertions.assertTrue(plugin.isIaCTechnologySupported("opentoscacontainer"));
        Assertions.assertTrue(plugin.isIaCTechnologySupported("random tech name"));
        Assertions.assertEquals(0, plugin.getRequiredProductionSystemPropertyNames().size());
        Assertions.assertEquals(1, plugin.getRequiredConfigurationEntryNames().size());
        Assertions.assertThrows(ConfigurationMissingException.class, () ->
                plugin.reconstructInstanceModel(new ProductionSystem("", "", new HashMap<>())));
        plugin.setConfigurationEntry(ManualModelCreatorPlugin.CONFIG_ENTRY_MODEL_PATH, "random path!");
        Assertions.assertThrows(RuntimeException.class, () ->
                plugin.reconstructInstanceModel(new ProductionSystem("", "", new HashMap<>())));
    }
}