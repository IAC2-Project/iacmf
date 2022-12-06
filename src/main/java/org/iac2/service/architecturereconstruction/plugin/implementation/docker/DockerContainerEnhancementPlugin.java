package org.iac2.service.architecturereconstruction.plugin.implementation.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.google.common.collect.Maps;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.HostedOn;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.compress.utils.Lists;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.utility.Edmm;
import org.iac2.common.utility.Utils;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelEnhancementPlugin;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerContainer;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerEngine;
import org.iac2.service.architecturereconstruction.common.model.StructuralState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DockerContainerEnhancementPlugin implements ModelEnhancementPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerContainerEnhancementPlugin.class);

    public static void addDockerContainerToEntityGraph(EntityGraph graph, RootComponent dockerEngineComponent, Container container) throws IllegalAccessException {
        // here we need to setup a proper mapping to the properties of a dockercontainer Node Type and so..
        // todo we need to ensure that we do not add components with the same id (...multiple engines)
        Map<String, Object> props = generateAttributes(container, StructuralState.NOT_EXPECTED);
        EntityId entityId = Edmm.addComponent(graph, container.getId(), props, DockerContainer.class);
        Edmm.addRelation(graph, entityId, dockerEngineComponent.getEntity().getId(), HostedOn.class);
    }

    private static Map<String, Object> generateAttributes(Container container, StructuralState state) {
        Map<String, Object> props = Maps.newHashMap();
        props.put("ContainerID", container.getId());
        props.put("structuralState", state.name());
        props.put("Image", container.getImage());

        return props;
    }

    @Override
    public Collection<String> getRequiredPropertyNames() {
        Collection<String> reqs = Lists.newArrayList();
        return reqs;
    }

    @Override
    public String getIdentifier() {
        return "docker-enhancement-plugin";
    }

    @Override
    public InstanceModel enhanceModel(InstanceModel instanceModel, ProductionSystem productionSystem) {

        DeploymentModel deploymentModel = instanceModel.getDeploymentModel();
        Collection<DockerEngine> dockerEngineComponents =
                Edmm.getAllComponentsOfType(deploymentModel, DockerEngine.class);

        // to filter for example the management components of the production system (opentosca runnning on the same docker engin...)
        Collection<String> containerImagesToFilter = productionSystem.getProperties().keySet()
                .stream()
                .filter(k -> k.startsWith("dockerContainerFilter"))
                .map(k -> productionSystem.getProperties().get(k))
                .toList();

        for (DockerEngine d : dockerEngineComponents) {
            String dockerEngineUrl = d.getProperty("DockerEngineURL").orElseThrow().getValue();

            if (dockerEngineUrl.contains("host.docker.internal")) {
                // this is a little dirty, as we use such an URL in the test environment,
                // we assume this URL is never like this but only a proper URL/IP
                // => TODO: FIXME
                dockerEngineUrl = dockerEngineUrl.replace("host.docker.internal", "localhost");
            }

            try (DockerClient dockerClient = Utils.createDockerClient(dockerEngineUrl)) {
                List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
                enhanceModel(deploymentModel, containerImagesToFilter, d, containers);
            } catch (IOException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return new InstanceModel(new DeploymentModel(deploymentModel.getName(), deploymentModel.getGraph()));
    }

    public void enhanceModel(DeploymentModel deploymentModel,
                             Collection<String> containerImagesToFilter,
                             DockerEngine dockerEngineComponent,
                             List<Container> containers) throws IllegalAccessException {

        EntityGraph graph = deploymentModel.getGraph();
        containers = containers.stream()
                .filter(c -> !containerImagesToFilter.contains(c.getImage()))
                .filter(c -> "running".equals(c.getState()))
                .collect(Collectors.toList());

        BidiMap<String, DockerContainer> edmmDockerContainers = new DualHashBidiMap<>();
        // we find the edmm docker containers hosted on this specific docker engine
        Collection<RootComponent> components = Edmm.findDependentComponents(deploymentModel, dockerEngineComponent, HostedOn.class)
                .stream()
                .filter(c -> c instanceof DockerContainer)
                .toList();
        components.forEach(c -> edmmDockerContainers.put(c.getProperty("ContainerID").orElseThrow().getValue(), (DockerContainer) c));

        Collection<String> actualDockerContainerIds = containers
                .stream()
                .map(Container::getId)
                .toList();
        RootComponent current;

        for (Container c : containers) {
            if (edmmDockerContainers.containsKey(c.getId())) {
                current = edmmDockerContainers.get(c.getId());
                Edmm.addPropertyAssignments(graph,
                        current.getEntity().getId(),
                        generateAttributes(c, StructuralState.EXPECTED));
            } else {
                addDockerContainerToEntityGraph(graph, dockerEngineComponent, c);
            }
        }

        // now we search for containers that should have been there!
        for (String deploymentModelContainerId : edmmDockerContainers.keySet()) {
            if (!actualDockerContainerIds.contains(deploymentModelContainerId)) {
                current = edmmDockerContainers.get(deploymentModelContainerId);
                Map<String, Object> props = new HashMap<>();
                props.put("structuralState", StructuralState.REMOVED.name());
                Edmm.addPropertyAssignments(graph,
                        current.getEntity().getId(),
                        props);
            }
        }
    }

    private Collection<Container> findContainersNotInDeploymentModel(DeploymentModel deploymentModel, Collection<Container> containers) {
        Collection<String> deploymentModelContainerIds =
                Edmm.getAllComponentsOfType(deploymentModel, DockerContainer.class)
                        .stream()
                        .map(c -> c.getProperty("ContainerID").orElseThrow().getValue())
                        .toList();

        return containers
                .stream()
                .filter(c -> !deploymentModelContainerIds.contains(c.getId()))
                .collect(Collectors.toList());
    }
}
