package org.iac2.service.checking.plugin.manager.implementation;

import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.common.model.compliancerule.ComplianceRule;
import org.iac2.service.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SimpleCRCheckingManagerTest {

    @Test
    void getPossiblePluginsForComplianceRule() {
        SimpleCRCheckingManager instance = SimpleCRCheckingManager.getInstance();
        ComplianceRuleCheckingPlugin plugin = instance.getPlugin("subgraph-matching-checking-plugin");
        assertNotNull(plugin);
        Assertions.assertThrows(PluginNotFoundException.class, () -> instance.getPlugin("abc"));
    }

    @Test
    void getPlugin() {
        SimpleCRCheckingManager instance = SimpleCRCheckingManager.getInstance();
        ComplianceRule cr = new ComplianceRule(1L, "subgraph-matching", "https://nowhere.no");
        Collection<ComplianceRuleCheckingPlugin> plugins = instance.getPossiblePluginsForComplianceRule(cr);
        assertNotNull(plugins);
        assertEquals(1, plugins.size());
    }
}
