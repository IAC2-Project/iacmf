package org.iac2.architecturereconstruction.service;

import org.iac2.architecturereconstruction.common.interfaces.ArchitectureReconstructionPlugin;
import org.iac2.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.architecturereconstruction.common.interfaces.ModelEnhancementPlugin;
import org.iac2.architecturereconstruction.plugin.manager.ArchitectureReconstructionPluginManager;
import org.iac2.common.model.ProductionSystem;

import java.util.Collection;
import java.util.List;

public class MockARPluginManager implements ArchitectureReconstructionPluginManager {
    @Override
    public ModelCreationPlugin getModelCreationPlugin(String identifier) {
        return new MockModelCreationPlugin(3);
    }

    @Override
    public Collection<ModelCreationPlugin> getPossibleModelCreationPluginsForProductionSystem(ProductionSystem productionSystem) {
        return List.of(new MockModelCreationPlugin(3));
    }

    @Override
    public ModelEnhancementPlugin getModelEnhancementPlugin(String identifier) {
        return null;
    }

    @Override
    public Collection<ArchitectureReconstructionPlugin> getAll() {
        return List.of(new MockModelCreationPlugin(3));
    }
}
