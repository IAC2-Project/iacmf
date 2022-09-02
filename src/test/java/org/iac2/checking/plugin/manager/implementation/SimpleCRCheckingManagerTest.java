package org.iac2.checking.plugin.manager.implementation;

import java.util.Collection;

import org.iac2.checking.common.interfaces.ComplianceRuleCheckingPlugin;
import org.iac2.checking.common.model.compliancerule.ComplianceRule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SimpleCRCheckingManagerTest {

    @Test
    void getPossiblePluginsForComplianceRule() {
        SimpleCRCheckingManager instance = SimpleCRCheckingManager.getInstance();
        ComplianceRuleCheckingPlugin plugin = instance.getPlugin("subgraphisomorphism");
        assertNotNull(plugin);
        plugin = instance.getPlugin("abc");
        assertNull(plugin);
    }

    @Test
    void getPlugin() {
        SimpleCRCheckingManager instance = SimpleCRCheckingManager.getInstance();
        ComplianceRule cr = new ComplianceRule("subgraphisomorphism", "https://nowhere.no");
        Collection<ComplianceRuleCheckingPlugin> plugins = instance.getPossiblePluginsForComplianceRule(cr);
        assertNotNull(plugins);
        assertEquals(1, plugins.size());
    }
}