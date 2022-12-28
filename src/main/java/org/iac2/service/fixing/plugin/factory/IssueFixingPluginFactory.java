package org.iac2.service.fixing.plugin.factory;

import java.util.Collection;

import org.iac2.common.PluginFactory;
import org.iac2.common.model.ProductionSystem;

public interface IssueFixingPluginFactory extends PluginFactory {
    Collection<String> getSuitablePluginIdentifiers(String issueType, ProductionSystem productionSystem);
}
