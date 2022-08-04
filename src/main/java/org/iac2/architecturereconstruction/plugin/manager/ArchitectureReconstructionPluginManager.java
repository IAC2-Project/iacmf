package org.iac2.architecturereconstruction.plugin.manager;

import java.util.Collection;

import org.iac2.architecturereconstruction.plugin.interfaces.ArchitectureReconstructionPlugin;
import org.iac2.architecturereconstruction.plugin.interfaces.ModelCreationPlugin;
import org.iac2.architecturereconstruction.plugin.interfaces.ModelEnhancementPlugin;
import org.iac2.common.model.ProductionSystem;

public interface ArchitectureReconstructionPluginManager {
    ModelCreationPlugin getModelCreationPlugin(String identifier);
    Collection<ModelCreationPlugin> getPossibleModelCreationPluginsForProductionSystem(ProductionSystem productionSystem);
    ModelEnhancementPlugin getModelEnhancementPlugin(String identifier);
    Collection<ArchitectureReconstructionPlugin> getAll();
}
