package org.iac2.fixing.plugin.manager;

import java.util.Collection;

import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.fixing.common.interfaces.IssueFixingPlugin;

public interface IssueFixingPluginManager {

    Collection<IssueFixingPlugin> getSuitablePlugins(ComplianceIssue complianceIssue, ProductionSystem productionSystem);

    IssueFixingPlugin getPlugin(String identifier);

    Collection<IssueFixingPlugin> getAll();
}
