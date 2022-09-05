package org.iac2.service.fixing.plugin.manager;

import java.util.Collection;

import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;
import org.iac2.service.fixing.common.interfaces.IssueFixingPlugin;

public interface IssueFixingPluginManager {

    Collection<IssueFixingPlugin> getSuitablePlugins(ComplianceIssue complianceIssue, ProductionSystem productionSystem);

    IssueFixingPlugin getPlugin(String identifier) throws PluginNotFoundException;

    Collection<IssueFixingPlugin> getAll();
}
