package org.iac2.service.fixing.plugin.manager.implementation;

import com.google.common.collect.Maps;
import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SimpleIssueFixingPluginManagerTest {

    @Test
    void getPlugin() {
        SimpleIssueFixingPluginManager instance = SimpleIssueFixingPluginManager.getInstance();
        IssueFixingPlugin plugin = instance.getPlugin("docker-container-issue-fixing-plugin");
        assertNotNull(plugin);
        assertThrows(PluginNotFoundException.class, () -> instance.getPlugin("abc"));
    }

    @Test
    void getSuitablePlugins() {
        SimpleIssueFixingPluginManager instance = SimpleIssueFixingPluginManager.getInstance();
        ComplianceRule rule = new ComplianceRule();

        Map<String, String> issueProps = Maps.newHashMap();
        issueProps.put("INSTANCE_MODEL_COMPONENT_ID", "bla");
        issueProps.put("CHECKER_COMPONENT_ID", "blub");
        ComplianceIssue issue = new ComplianceIssue(
                "I have a bad feeling about this!",
                rule,
                "WrongAttributeValueIssue",
                issueProps);
        ProductionSystem productionSystem = new ProductionSystem(
                "opentoscacontainer",
                "bla bla",
                new HashMap<>());
        Collection<IssueFixingPlugin> plugins = instance.getSuitablePlugins(issue, productionSystem);
        assertNotNull(plugins);
        assertEquals(1, plugins.size());
    }
}
