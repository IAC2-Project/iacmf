package org.iac2.service.fixing.plugin.factory.implementation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;
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
        SimpleIssueFixingPluginFactory instance = SimpleIssueFixingPluginFactory.getInstance();
        IssueFixingPlugin plugin = instance.createPlugin("docker-container-issue-fixing-plugin");
        assertNotNull(plugin);
        assertThrows(PluginNotFoundException.class, () -> instance.createPlugin("abc"));
    }

    @Test
    void getSuitablePlugins() {
        SimpleIssueFixingPluginFactory instance = SimpleIssueFixingPluginFactory.getInstance();
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
        Collection<String> plugins = instance.getSuitablePluginIdentifiers(issue.getType(), productionSystem);
        assertNotNull(plugins);
        assertEquals(1, plugins.size());
    }
}
