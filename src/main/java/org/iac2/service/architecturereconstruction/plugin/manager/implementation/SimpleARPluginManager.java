package org.iac2.service.architecturereconstruction.plugin.manager.implementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.common.model.PluginType;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.architecturereconstruction.common.interfaces.ArchitectureReconstructionPlugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelEnhancementPlugin;
import org.iac2.service.architecturereconstruction.plugin.implementation.docker.DockerContainerEnhancementPlugin;
import org.iac2.service.architecturereconstruction.plugin.implementation.manual.ManualModelCreatorPlugin;
import org.iac2.service.architecturereconstruction.plugin.implementation.opentoscacontainer.OpenToscaContainerModelCreationPlugin;
import org.iac2.service.architecturereconstruction.plugin.manager.ArchitectureReconstructionPluginManager;

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
        DockerContainerEnhancementPlugin dockerContainerEnhancementPlugin = new DockerContainerEnhancementPlugin();
        this.modelCreationPluginMap.put(manualPlugin.getIdentifier(), manualPlugin);
        this.modelCreationPluginMap.put(openToscaContainerPlugin.getIdentifier(), openToscaContainerPlugin);
        this.modelEnhancementPluginMap.put(dockerContainerEnhancementPlugin.getIdentifier(), dockerContainerEnhancementPlugin);
    }

    @Override
    public ModelCreationPlugin getModelCreationPlugin(String identifier) {
        ModelCreationPlugin plugin = this.modelCreationPluginMap.get(identifier);

        if (plugin == null) {
            throw new PluginNotFoundException(identifier, PluginType.MODEL_CREATION);
        }

        return plugin;
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
        ModelEnhancementPlugin plugin = this.modelEnhancementPluginMap.get(identifier);

        if (plugin == null) {
            throw new PluginNotFoundException(identifier, PluginType.ISSUE_CHECKING);
        }

        return plugin;
    }

    @Override
    public Collection<ArchitectureReconstructionPlugin> getAll() {

        List<ArchitectureReconstructionPlugin> result = new ArrayList<>(this.modelCreationPluginMap.values());
        result.addAll(this.modelEnhancementPluginMap.values());

        return result;
    }

    @Override
    public boolean modelCreationPluginExists(String identifier) {
        return this.modelCreationPluginMap.containsKey(identifier);
    }

    @Override
    public boolean modelRefinementPluginExists(String identifier) {
        return this.modelEnhancementPluginMap.containsKey(identifier);
    }
}
