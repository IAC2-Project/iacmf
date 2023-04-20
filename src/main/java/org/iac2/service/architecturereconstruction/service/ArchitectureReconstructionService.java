package org.iac2.service.architecturereconstruction.service;

import io.kubernetes.client.ApiException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.entity.compliancejob.execution.ExecutionEntity;
import org.iac2.entity.plugin.PluginUsageEntity;
import org.iac2.entity.productionsystem.ProductionSystemEntity;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelRefinementPlugin;
import org.iac2.service.architecturereconstruction.plugin.factory.ArchitectureReconstructionPluginFactory;
import org.iac2.service.utility.PluginConfigurationHelperService;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.FileNotFoundException;
import java.util.List;

import static org.iac2.service.utility.EntityToPojo.transformProductionSystemEntity;

@Service
public class ArchitectureReconstructionService {

    private final ArchitectureReconstructionPluginFactory pluginManager;

    private final PluginConfigurationHelperService helperService;

    public ArchitectureReconstructionService(ArchitectureReconstructionPluginFactory pluginManager, PluginConfigurationHelperService helperService) {
        this.pluginManager = pluginManager;
        this.helperService = helperService;
    }

    public InstanceModel crteateInstanceModel(@NotNull ProductionSystemEntity productionSystemEntity, ExecutionEntity execution) throws FileNotFoundException, ApiException {
        ProductionSystem productionSystem = transformProductionSystemEntity(productionSystemEntity);
        PluginUsageEntity pluginUsage = productionSystemEntity.getModelCreationPluginUsage();
        ModelCreationPlugin plugin = (ModelCreationPlugin) helperService.instantiatePlugin(pluginUsage, execution, pluginManager);

        return plugin.reconstructInstanceModel(productionSystem);
    }

    public void refineInstanceModel(@NotNull ExecutionEntity execution,
                                    @NotNull InstanceModel instanceModel) {
        List<PluginUsageEntity> usages = execution.getComplianceJob().getModelRefinementStrategy();

        this.refineInstanceModel(
                usages,
                execution,
                instanceModel);
    }

    public void refineInstanceModel(@NotNull List<PluginUsageEntity> enhancementStrategy,
                                    @NotNull ExecutionEntity execution,
                                    @NotNull InstanceModel instanceModel) {
        ProductionSystem productionSystem = transformProductionSystemEntity(execution.getComplianceJob().getProductionSystem());
        List<ModelRefinementPlugin> plugins = enhancementStrategy
                .stream()
                .map(usage -> (ModelRefinementPlugin) helperService.instantiatePlugin(usage, execution, pluginManager))
                .toList();

        for (ModelRefinementPlugin plugin : plugins) {
            plugin.refineModel(instanceModel, productionSystem);
        }
    }

    public ArchitectureReconstructionPluginFactory getPluginManager() {
        return this.pluginManager;
    }
}
