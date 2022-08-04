package org.iac2.architecturereconstruction.plugin.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.iac2.architecturereconstruction.plugin.implementation.manual.ManualModelCreatorPlugin;
import org.iac2.architecturereconstruction.plugin.implementation.opentoscacontainer.OpenToscaContainerPlugin;
import org.iac2.architecturereconstruction.plugin.interfaces.ArchitectureReconstructionPlugin;
import org.iac2.architecturereconstruction.plugin.interfaces.ModelCreationPlugin;
import org.iac2.architecturereconstruction.plugin.interfaces.ModelEnhancementPlugin;
import org.iac2.common.model.ProductionSystem;

public class SimpleARPluginManager implements ArchitectureReconstructionPluginManager {
    private static SimpleARPluginManager instance;

    private SimpleARPluginManager() {
        this.modelCreationPluginMap = new HashMap<>();
        this.modelEnhancementPluginMap = new HashMap<>();
        initializePlugins();
    }

    public static SimpleARPluginManager getInstance() {
        if (instance == null) {
            instance = new SimpleARPluginManager();
        }

        return instance;
    }

    private Map<String, ModelCreationPlugin> modelCreationPluginMap;
    private Map<String, ModelEnhancementPlugin> modelEnhancementPluginMap;

    private void initializePlugins() {
        // here instances of plugins are created.
        ManualModelCreatorPlugin manualPlugin = new ManualModelCreatorPlugin();
        OpenToscaContainerPlugin openToscaContainerPlugin = new OpenToscaContainerPlugin();
        this.modelCreationPluginMap.put(manualPlugin.getIdentifier(), manualPlugin);
        this.modelCreationPluginMap.put(openToscaContainerPlugin.getIdentifier(), openToscaContainerPlugin);
    }

    @Override
    public ModelCreationPlugin getModelCreationPlugin(String identifier) {
        return this.modelCreationPluginMap.get(identifier);
    }

    @Override
    public Collection<ModelCreationPlugin> getPossibleModelCreationPluginsForProductionSystem(ProductionSystem productionSystem) {
        String iacTechnology = productionSystem.getIacTechnologyName();

        return this.modelCreationPluginMap
                .values()
                .stream()
                .filter(plugin -> plugin.isIaCTechnologySupported(iacTechnology))
                .collect(Collectors.toList());
    }

    @Override
    public ModelEnhancementPlugin getModelEnhancementPlugin(String identifier) {
        return this.modelEnhancementPluginMap.get(identifier);
    }

    @Override
    public Collection<ArchitectureReconstructionPlugin> getAll() {

        List<ArchitectureReconstructionPlugin> result = new ArrayList<>(this.modelCreationPluginMap.values());
        result.addAll(this.modelEnhancementPluginMap.values());

        return result;
    }
}
