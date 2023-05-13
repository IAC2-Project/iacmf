package org.iac2.service.fixing.plugin.factory.implementation;

import java.util.Collection;
import java.util.HashMap;

import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.iac2.service.fixing.plugin.implementaiton.docker.DockerContainerIssueFixingPluginDescriptor;
import org.iac2.service.fixing.plugin.implementaiton.mysql.RemoveDBUsersFixingPluginDescriptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleIssueFixingPluginManagerTest {

    @Test
    void getPlugin() {
        SimpleIssueFixingPluginFactory instance = SimpleIssueFixingPluginFactory.getInstance();
        IssueFixingPlugin plugin = instance.createPlugin("docker-container-issue-fixing-plugin");
        assertNotNull(plugin);
        assertThrows(PluginNotFoundException.class, () -> instance.createPlugin("abc"));
    }

    @Test
    void getSuitablePlugins() {
        SimpleIssueFixingPluginFactory instance = SimpleIssueFixingPluginFactory.getInstance();
        String issueType = DockerContainerIssueFixingPluginDescriptor.SUPPORTED_ISSUE_TYPES[0];
        ProductionSystem productionSystem = new ProductionSystem(
                "dummy",
                "opentoscacontainer",
                "bla bla",
                new HashMap<>());
        Collection<String> plugins = instance.getSuitablePluginIdentifiers(issueType, productionSystem);
        assertNotNull(plugins);
        assertEquals(2, plugins.size());
        issueType = RemoveDBUsersFixingPluginDescriptor.SUPPORTED_ISSUE_TYPES[0];
        plugins = instance.getSuitablePluginIdentifiers(issueType, productionSystem);
        assertNotNull(plugins);
        assertEquals(2, plugins.size());
    }
}
