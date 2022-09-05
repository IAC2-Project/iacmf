package org.iac2.service.architecturereconstruction.service;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.InstanceModel;
import org.iac2.entity.compliancejob.ComplianceJobEntity;
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

    public InstanceModel reconstructArchitectureForProductionSystem(ProductionSystemEntity productionSystemEntity) {
        ProductionSystem productionSystem = transformProductionSystemEntity(productionSystemEntity);
        InstanceModel systemModel = createInstanceModel(productionSystemEntity.getModelCreationPluginId(), productionSystem);
        enhanceInstanceModel(productionSystemEntity.getModelEnhancementStrategy().getPluginIdList(),
                productionSystem,
                systemModel);

        return systemModel;
    }

    public void enhanceArchitectureForComplianceJob(@NotNull ComplianceJobEntity complianceJob, @NotNull InstanceModel systemModel) {
        ProductionSystem productionSystem = transformProductionSystemEntity(complianceJob.getProductionSystem());
        enhanceInstanceModel(complianceJob.getModelEnhancementStrategy().getPluginIdList(),
                productionSystem,
                systemModel);
    }

    private InstanceModel createInstanceModel(String pluginId, ProductionSystem productionSystem) {
        ModelCreationPlugin plugin = pluginManager.getModelCreationPlugin(pluginId);

        return plugin.reconstructInstanceModel(productionSystem);
    }

    private void enhanceInstanceModel(List<String> pluginIds, ProductionSystem productionSystem, InstanceModel currentSystemModel) {
        List<ModelEnhancementPlugin> plugins = pluginIds
                .stream()
                .map(pluginManager::getModelEnhancementPlugin)
                .toList();

        for (ModelEnhancementPlugin plugin : plugins) {
            plugin.enhanceModel(currentSystemModel, productionSystem);
        }
    }
}
