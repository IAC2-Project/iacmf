package org.iac2.architecturereconstruction.plugin.manager;

import org.iac2.architecturereconstruction.common.interfaces.ArchitectureReconstructionPlugin;
import org.iac2.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.architecturereconstruction.common.interfaces.ModelEnhancementPlugin;
import org.iac2.common.model.ProductionSystem;

import java.util.Collection;

public interface ArchitectureReconstructionPluginManager {
    ModelCreationPlugin getModelCreationPlugin(String identifier);

    Collection<ModelCreationPlugin> getPossibleModelCreationPluginsForProductionSystem(ProductionSystem productionSystem);

    ModelEnhancementPlugin getModelEnhancementPlugin(String identifier);

    Collection<ArchitectureReconstructionPlugin> getAll();
}
