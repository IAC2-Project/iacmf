package org.iac2.service.architecturereconstruction.plugin.factory;

import java.util.Collection;

import org.iac2.common.PluginFactory;
import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelRefinementPlugin;

public interface ArchitectureReconstructionPluginFactory extends PluginFactory {
    ModelCreationPlugin createModelCreationPlugin(String identifier) throws PluginNotFoundException;

    Collection<String> getPossibleModelCreationPluginIdentifiersForProductionSystem(ProductionSystem productionSystem);

    ModelRefinementPlugin createModelRefinementPlugin(String identifier) throws PluginNotFoundException;

    boolean modelCreationPluginExists(String identifier);

    boolean modelRefinementPluginExists(String identifier);
}
