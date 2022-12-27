package org.iac2.service.architecturereconstruction.plugin.factory.implementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.iac2.common.Plugin;
import org.iac2.common.PluginDescriptor;
import org.iac2.common.exception.PluginNotFoundException;
import org.iac2.common.model.PluginType;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPluginDescriptor;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelRefinementPlugin;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelRefinementPluginDescriptor;
import org.iac2.service.architecturereconstruction.plugin.factory.ArchitectureReconstructionPluginFactory;
import org.iac2.service.architecturereconstruction.plugin.implementation.docker.DockerContainerRefinementPluginDescriptor;
import org.iac2.service.architecturereconstruction.plugin.implementation.manual.ManualModelCreationPluginDescriptor;
import org.iac2.service.architecturereconstruction.plugin.implementation.opentoscacontainer.OpenToscaContainerModelCreationPluginDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Plugin manager with hard-coded plugin classes.
 * This class is provided as a bean (singleton). Check the application class.
 */
public class SimpleARPluginFactory implements ArchitectureReconstructionPluginFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleARPluginFactory.class);
    private static SimpleARPluginFactory instance;
    private final Map<String, ModelCreationPluginDescriptor> modelCreationPluginMap;
    private final Map<String, ModelRefinementPluginDescriptor> modelRefinementPluginMap;

    private SimpleARPluginFactory() {
        this.modelCreationPluginMap = new HashMap<>();
        this.modelRefinementPluginMap = new HashMap<>();
        initializePlugins();
    }

    public static SimpleARPluginFactory getInstance() {
        if (instance == null) {
            instance = new SimpleARPluginFactory();
        }

        return instance;
    }

    private void initializePlugins() {
        ManualModelCreationPluginDescriptor manual = new ManualModelCreationPluginDescriptor();
        OpenToscaContainerModelCreationPluginDescriptor openTosca = new OpenToscaContainerModelCreationPluginDescriptor();
        DockerContainerRefinementPluginDescriptor docker = new DockerContainerRefinementPluginDescriptor();
        // here instances of plugins are created.
        this.modelCreationPluginMap.put(manual.getIdentifier(), manual);
        this.modelCreationPluginMap.put(openTosca.getIdentifier(), openTosca);
        this.modelRefinementPluginMap.put(docker.getIdentifier(), docker);
    }

    @Override
    public ModelCreationPlugin createModelCreationPlugin(String identifier) {

        PluginDescriptor pluginDescriptor = this.modelCreationPluginMap.get(identifier);

        if (pluginDescriptor == null) {
            throw new PluginNotFoundException(identifier, PluginType.MODEL_CREATION);
        }

        return (ModelCreationPlugin) pluginDescriptor.createPlugin();
    }

    @Override
    public Collection<String> getPossibleModelCreationPluginIdentifiersForProductionSystem(ProductionSystem productionSystem) {
        String iacTechnology = productionSystem.getIacTechnologyName();

        return this.modelCreationPluginMap
                .values()
                .stream()
                .filter(pluginD -> pluginD.isIaCTechnologySupported(iacTechnology))
                .map(PluginDescriptor::getIdentifier)
                .collect(Collectors.toList());
    }

    @Override
    public ModelRefinementPlugin createModelRefinementPlugin(String identifier) {
        ModelRefinementPluginDescriptor pluginD = this.modelRefinementPluginMap.get(identifier);

        if (pluginD == null) {
            throw new PluginNotFoundException(identifier, PluginType.MODEL_REFINEMENT);
        }

        return (ModelRefinementPlugin) pluginD.createPlugin();
    }

    @Override
    public Plugin createPlugin(String identifier) throws PluginNotFoundException {
        if (modelCreationPluginExists(identifier)) {
            return createModelCreationPlugin(identifier);
        }

        return createModelRefinementPlugin(identifier);
    }

    @Override
    public Collection<String> getAllPluginIdentifiers() {

        List<PluginDescriptor> result = new ArrayList<>(this.modelCreationPluginMap.values());
        result.addAll(this.modelRefinementPluginMap.values());

        return result.stream().map(PluginDescriptor::getIdentifier).toList();
    }

    @Override
    public PluginDescriptor describePlugin(String identifier) {
        return modelCreationPluginExists(identifier) ? modelCreationPluginMap.get(identifier) : modelRefinementPluginMap.get(identifier);
    }

    @Override
    public boolean pluginExists(String identifier) {
        return modelRefinementPluginExists(identifier) || modelCreationPluginExists(identifier);
    }

    @Override
    public boolean modelCreationPluginExists(String identifier) {
        return this.modelCreationPluginMap.containsKey(identifier);
    }

    @Override
    public boolean modelRefinementPluginExists(String identifier) {
        return this.modelRefinementPluginMap.containsKey(identifier);
    }
}
