package org.iac2.service.architecturereconstruction.plugin.factory;

import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.plugin.factory.implementation.SimpleARPluginFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SimpleARPluginManagerTest {
    @Test
    void getModelCreationPlugin() {
        SimpleARPluginFactory instance = SimpleARPluginFactory.getInstance();
        ModelCreationPlugin plugin = instance.createModelCreationPlugin("opentosca-container-model-creation-plugin");
        assertNotNull(plugin);
        assertEquals("opentosca-container-model-creation-plugin", plugin.getIdentifier());
        Assertions.assertThrows(PluginNotFoundException.class, () -> instance.createModelCreationPlugin("abc"));
    }
}
