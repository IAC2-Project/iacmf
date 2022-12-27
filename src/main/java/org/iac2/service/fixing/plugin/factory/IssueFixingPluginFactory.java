package org.iac2.service.fixing.plugin.factory;

import java.util.Collection;

import org.iac2.common.PluginFactory;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.compliancejob.issue.ComplianceIssue;

public interface IssueFixingPluginFactory extends PluginFactory {
    Collection<String> getSuitablePluginIdentifiers(ComplianceIssue complianceIssue, ProductionSystem productionSystem);
}
