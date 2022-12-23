package org.iac2.service.architecturereconstruction.service;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
import org.iac2.entity.plugin.architecturereconstruction.ModelEnhancementStrategyEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelEnhancementPlugin;
import org.iac2.service.architecturereconstruction.plugin.manager.ArchitectureReconstructionPluginManager;
import org.springframework.stereotype.Service;

import static org.iac2.service.utility.EntityToPojo.transformProductionSystemEntity;

@Service
public class ArchitectureReconstructionService {

    private final ArchitectureReconstructionPluginManager pluginManager;

    public ArchitectureReconstructionService(ArchitectureReconstructionPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public InstanceModel crteateInstanceModel(@NotNull ProductionSystemEntity productionSystemEntity) {
        ProductionSystem productionSystem = transformProductionSystemEntity(productionSystemEntity);
        ModelCreationPlugin plugin = pluginManager.getModelCreationPlugin(productionSystemEntity.getModelCreationPluginId());

        return plugin.reconstructInstanceModel(productionSystem);
    }

    public void refineInstanceModel(@NotNull ComplianceJobEntity complianceJob,
                                    @NotNull InstanceModel instanceModel) {
        this.refineInstanceModel(
                complianceJob.getModelEnhancementStrategy(),
                complianceJob.getProductionSystem(),
                instanceModel);
    }

    public void refineInstanceModel(@NotNull ModelEnhancementStrategyEntity enhancementStrategy,
                                    @NotNull ProductionSystemEntity productionSystemEntity,
                                    @NotNull InstanceModel instanceModel) {
        ProductionSystem productionSystem = transformProductionSystemEntity(productionSystemEntity);
        List<ModelEnhancementPlugin> plugins = enhancementStrategy.getPluginIdList()
                .stream()
                .map(pluginManager::getModelEnhancementPlugin)
                .toList();

        for (ModelEnhancementPlugin plugin : plugins) {
            plugin.enhanceModel(instanceModel, productionSystem);
        }
    }

    public ArchitectureReconstructionPluginManager getPluginManager() {
        return this.pluginManager;
    }
}
