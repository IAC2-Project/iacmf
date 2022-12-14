package org.iac2.service.architecturereconstruction.plugin.manager;

import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.plugin.manager.implementation.SimpleARPluginManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SimpleARPluginManagerTest {
    @Test
    void getModelCreationPlugin() {
        SimpleARPluginManager instance = SimpleARPluginManager.getInstance();
        ModelCreationPlugin plugin = instance.getModelCreationPlugin("opentosca-container-model-creation-plugin");
        assertNotNull(plugin);
        assertEquals("opentosca-container-model-creation-plugin", plugin.getIdentifier());
        Assertions.assertThrows(PluginNotFoundException.class, () -> instance.getModelCreationPlugin("abc"));
    }

}
