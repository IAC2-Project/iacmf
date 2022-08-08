package org.iac2.architecturereconstruction.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.iac2.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.architecturereconstruction.common.interfaces.ModelEnhancementPlugin;
import org.iac2.architecturereconstruction.plugin.manager.ArchitectureReconstructionPluginManager;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.SystemModel;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.springframework.stereotype.Service;

@Service
public class ArchitectureReconstructionService {

    private final ArchitectureReconstructionPluginManager pluginManager;

    public ArchitectureReconstructionService(ArchitectureReconstructionPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public SystemModel reconstructArchitectureForProductionSystem(ProductionSystemEntity productionSystemEntity) {
        ProductionSystem productionSystem = transformProductionSystemEntity(productionSystemEntity);
        SystemModel systemModel = createInstanceModel(productionSystemEntity.getModelCreationPluginId(), productionSystem);
        enhanceInstanceModel(productionSystemEntity.getModelEnhancementStrategy().getPluginIdList(),
                productionSystem,
                systemModel);

        return systemModel;
    }

    public void enhanceArchitectureForComplianceJob(@NotNull ComplianceJobEntity complianceJob, @NotNull SystemModel systemModel) {
        ProductionSystem productionSystem = transformProductionSystemEntity(complianceJob.getProductionSystem());
        enhanceInstanceModel(complianceJob.getModelEnhancementStrategy().getPluginIdList(),
                productionSystem,
                systemModel);
    }

    private ProductionSystem transformProductionSystemEntity(ProductionSystemEntity productionSystemEntity) {
        Map<String, String> properties = new HashMap<>();
        productionSystemEntity.getKvPairs().forEach(kvPair -> properties.put(kvPair.getKey(), kvPair.getValue()));
        return new ProductionSystem(productionSystemEntity.getIacTechnologyName(),
                productionSystemEntity.getDescription(), properties);
    }

    private SystemModel createInstanceModel(String pluginId, ProductionSystem productionSystem) {
        ModelCreationPlugin plugin = pluginManager.getModelCreationPlugin(pluginId);

        return plugin.reconstructInstanceModel(productionSystem);
    }

    private void enhanceInstanceModel(List<String> pluginIds, ProductionSystem productionSystem, SystemModel currentSystemModel) {
        List<ModelEnhancementPlugin> plugins = pluginIds
                .stream()
                .map(pluginManager::getModelEnhancementPlugin)
                .toList();

        for (ModelEnhancementPlugin plugin : plugins) {
            plugin.enhanceModel(currentSystemModel, productionSystem);
        }
    }
}
