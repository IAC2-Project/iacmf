package org.iac2.service.architecturereconstruction.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.plugin.PluginUsageEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelEnhancementPlugin;
import org.iac2.service.architecturereconstruction.plugin.manager.ArchitectureReconstructionPluginManager;
import org.iac2.service.utility.PluginConfigurationHelper;
import org.springframework.stereotype.Service;

import static org.iac2.service.utility.EntityToPojo.transformProductionSystemEntity;

@Service
public class ArchitectureReconstructionService {

    private final ArchitectureReconstructionPluginManager pluginManager;
    @PersistenceContext
    EntityManager entityManager;

    public ArchitectureReconstructionService(ArchitectureReconstructionPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public InstanceModel crteateInstanceModel(@NotNull ProductionSystemEntity productionSystemEntity, ExecutionEntity execution) {
        ProductionSystem productionSystem = transformProductionSystemEntity(productionSystemEntity);
        PluginUsageEntity pluginUsage = productionSystemEntity.getModelCreationPluginUsage();
        ModelCreationPlugin plugin = (ModelCreationPlugin) PluginConfigurationHelper.instantiatePlugin(pluginUsage, execution, entityManager, pluginManager);

        return plugin.reconstructInstanceModel(productionSystem);
    }

    public void refineInstanceModel(@NotNull ExecutionEntity execution,
                                    @NotNull InstanceModel instanceModel) {
        List<PluginUsageEntity> usages = execution.getComplianceJob().getModelEnhancementStrategy();

        this.refineInstanceModel(
                usages,
                execution,
                instanceModel);
    }

    public void refineInstanceModel(@NotNull List<PluginUsageEntity> enhancementStrategy,
                                    @NotNull ExecutionEntity execution,
                                    @NotNull InstanceModel instanceModel) {
        ProductionSystem productionSystem = transformProductionSystemEntity(execution.getComplianceJob().getProductionSystem());
        List<ModelEnhancementPlugin> plugins = enhancementStrategy
                .stream()
                .map(usage -> (ModelEnhancementPlugin) PluginConfigurationHelper.instantiatePlugin(usage, execution, entityManager, pluginManager))
                .toList();

        for (ModelEnhancementPlugin plugin : plugins) {
            plugin.enhanceModel(instanceModel, productionSystem);
        }
    }

    public ArchitectureReconstructionPluginManager getPluginManager() {
        return this.pluginManager;
    }
}
