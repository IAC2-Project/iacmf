package org.iac2.service.fixing.plugin.manager.implementation;

import java.util.Collection;
import java.util.HashMap;

import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleIssueFixingPluginManagerTest {

    @Test
    void getPlugin() {
        SimpleIssueFixingPluginManager instance = SimpleIssueFixingPluginManager.getInstance();
        IssueFixingPlugin plugin = instance.getPlugin("opentosca-container-issue-fixing-plugin");
        assertNotNull(plugin);
        assertThrows(PluginNotFoundException.class, () ->instance.getPlugin("abc"));
    }

    @Test
    void getSuitablePlugins() {
        SimpleIssueFixingPluginManager instance = SimpleIssueFixingPluginManager.getInstance();
        ComplianceRule rule = new ComplianceRule();
        ComplianceIssue issue = new ComplianceIssue(
                "I have a bad feeling about this!",
                rule,
                "instance-matches-model",
                new HashMap<>());
        ProductionSystem productionSystem = new ProductionSystem(
                "opentoscacontainer",
                "bla bla",
                new HashMap<>());
        Collection<IssueFixingPlugin> plugins = instance.getSuitablePlugins(issue, productionSystem);
        assertNotNull(plugins);
        assertEquals(1, plugins.size());
    }
}