package org.iac2.architecturereconstruction.plugin.manager.implementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.iac2.architecturereconstruction.common.interfaces.ArchitectureReconstructionPlugin;
import org.iac2.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.architecturereconstruction.common.interfaces.ModelEnhancementPlugin;
import org.iac2.architecturereconstruction.plugin.implementation.manual.ManualModelCreatorPlugin;
import org.iac2.architecturereconstruction.plugin.implementation.opentoscacontainer.OpenToscaContainerModelCreationPlugin;
import org.iac2.architecturereconstruction.plugin.manager.ArchitectureReconstructionPluginManager;
import org.iac2.common.model.ProductionSystem;

/**
 * Plugin manager with hard-coded plugin classes.
 * This class is provided as a bean (singleton). Check the application class.
 */
public class SimpleARPluginManager implements ArchitectureReconstructionPluginManager {
    private static SimpleARPluginManager instance;
    private final Map<String, ModelCreationPlugin> modelCreationPluginMap;
    private final Map<String, ModelEnhancementPlugin> modelEnhancementPluginMap;

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

    private void initializePlugins() {
        // here instances of plugins are created.
        ManualModelCreatorPlugin manualPlugin = new ManualModelCreatorPlugin();
        OpenToscaContainerModelCreationPlugin openToscaContainerPlugin = new OpenToscaContainerModelCreationPlugin();
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
