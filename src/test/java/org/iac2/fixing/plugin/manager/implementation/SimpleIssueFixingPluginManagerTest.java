package org.iac2.fixing.plugin.manager.implementation;

import java.util.Collection;
import java.util.HashMap;

import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ArchitecturalComplianceIssue;
import org.iac2.common.model.compliancejob.issue.ArchitecturalIssueType;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.fixing.common.interfaces.IssueFixingPlugin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SimpleIssueFixingPluginManagerTest {

    @Test
    void getPlugin() {
        SimpleIssueFixingPluginManager instance = SimpleIssueFixingPluginManager.getInstance();
        IssueFixingPlugin plugin = instance.getPlugin("opentosca-container-issue-fixing-plugin");
        assertNotNull(plugin);
        plugin = instance.getPlugin("abc");
        assertNull(plugin);
    }

    @Test
    void getSuitablePlugins() {
        SimpleIssueFixingPluginManager instance = SimpleIssueFixingPluginManager.getInstance();
        ComplianceIssue issue = new ArchitecturalComplianceIssue(
                "I have a bad feeling about this!",
                "node-1/ubuntu-2/ec2-5",
                ArchitecturalIssueType.EXTRA_NODE);
        ProductionSystem productionSystem = new ProductionSystem(
                "opentoscacontainer",
                "bla bla",
                new HashMap<>());
        Collection<IssueFixingPlugin> plugins = instance.getSuitablePlugins(issue, productionSystem);
        assertNotNull(plugins);
        assertEquals(1, plugins.size());
    }

}