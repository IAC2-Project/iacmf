package org.iac2.service.fixing.plugin.manager;

import java.util.Collection;

import org.iac2.common.PluginManager;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;

public interface IssueFixingPluginManager extends PluginManager {
    Collection<IssueFixingPlugin> getSuitablePlugins(ComplianceIssue complianceIssue, ProductionSystem productionSystem);
}
