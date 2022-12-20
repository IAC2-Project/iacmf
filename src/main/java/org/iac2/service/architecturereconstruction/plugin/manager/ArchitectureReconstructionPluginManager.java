package org.iac2.service.architecturereconstruction.plugin.manager;

import java.util.Collection;

import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.architecturereconstruction.common.interfaces.ArchitectureReconstructionPlugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelEnhancementPlugin;

public interface ArchitectureReconstructionPluginManager {
    ModelCreationPlugin getModelCreationPlugin(String identifier) throws PluginNotFoundException;

    Collection<ModelCreationPlugin> getPossibleModelCreationPluginsForProductionSystem(ProductionSystem productionSystem);

    ModelEnhancementPlugin getModelEnhancementPlugin(String identifier) throws PluginNotFoundException;

    Collection<ArchitectureReconstructionPlugin> getAll();

    boolean modelCreationPluginExists(String identifier);

    boolean modelRefinementPluginExists(String identifier);
}
