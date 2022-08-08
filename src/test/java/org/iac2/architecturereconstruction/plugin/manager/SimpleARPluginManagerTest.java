package org.iac2.architecturereconstruction.plugin.manager;

import org.iac2.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.architecturereconstruction.plugin.manager.implementations.SimpleARPluginManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleARPluginManagerTest {

    @Test
    void getModelCreationPlugin() {
        SimpleARPluginManager instance = SimpleARPluginManager.getInstance();
        ModelCreationPlugin plugin = instance.getModelCreationPlugin("opentoscacontainerplugin");
        assertNotNull(plugin);
        assertEquals("opentoscacontainerplugin", plugin.getIdentifier());
        plugin = instance.getModelCreationPlugin("abc");
        assertNull(plugin);
    }

    @Test
    void getPossibleModelCreationPluginsForProductionSystem() {
    }
    

    @Test
    void getAll() {
    }
}