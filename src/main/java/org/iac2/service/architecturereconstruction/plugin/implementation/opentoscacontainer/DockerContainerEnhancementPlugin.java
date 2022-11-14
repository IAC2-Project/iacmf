package org.iac2.service.architecturereconstruction.plugin.implementation.opentoscacontainer;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.google.common.collect.Maps;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.HostedOn;
import org.apache.commons.compress.utils.Lists;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelEnhancementPlugin;
import org.iac2.common.utility.Edmm;
import org.iac2.service.utility.Utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DockerContainerEnhancementPlugin implements ModelEnhancementPlugin {
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

        DeploymentModel newDeploymentModel = instanceModel.getDeploymentModel();
        Collection<RootComponent> dockerEngineComponents = Edmm.getDockerEngineComponents(newDeploymentModel);

        // to filter for example the management components of the production system (opentosca runnning on the same docker engin...)
        Collection<String> containerImagesToFilter = productionSystem.getProperties().keySet().stream().filter(k -> k.startsWith("dockerContainerFilter")).map(k -> productionSystem.getProperties().get(k)).collect(Collectors.toList());

        for (RootComponent d : dockerEngineComponents) {
            String dockerEngineUrl = d.getProperty("DockerEngineURL").get().getValue();

            if (dockerEngineUrl.contains("host.docker.internal")) {
                // this is a little dirty, as we use such an URL in the test environment,
                // we assume this URL is never like this but only a proper URL/IP
                // => TODO: FIXME
                dockerEngineUrl = dockerEngineUrl.replace("host.docker.internal", "localhost");
            }

            DockerClient dockerClient = Utils.createDockerClient(dockerEngineUrl);

            List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();

            containers = containers.stream()
                    .filter(c -> !containerImagesToFilter.contains(c.getImage()))
                    .filter(c -> c.getState().equals("running"))
                    .collect(Collectors.toList());


            Collection<Container> containersNotInModel = this.findContainersNotInDeploymentModel(instanceModel.getDeploymentModel(), containers);

            for (Container c : containersNotInModel) {
                // a bit hacky i know....
                try {
                    newDeploymentModel = this.addDockerContainerToDeploymentModel(instanceModel.getDeploymentModel(), d, c);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return new InstanceModel(newDeploymentModel);
    }



    private DeploymentModel addDockerContainerToDeploymentModel(DeploymentModel deploymentModel, RootComponent dockerEngineComponent, Container container) throws IllegalAccessException {
        // here we need to setup a proper mapping to the properties of a dockercontainer Node Type and so..
        String dockerComponentId = container.getId();
        Map<String, String> props = Maps.newHashMap();
        props.put("ContainerID", container.getId());
        Class componentType = RootComponent.class;
        EntityId entityId = Edmm.addComponent(deploymentModel, dockerComponentId, props, componentType);
        Edmm.addRelation(deploymentModel, entityId, dockerEngineComponent.getEntity().getId(), HostedOn.class);
        return deploymentModel;
    }



    private Collection<Container> findContainersNotInDeploymentModel(DeploymentModel deploymentModel, Collection<Container> containers) {
        Collection<String> deploymentModelContainerIds = deploymentModel.getComponents().stream().filter(c -> c.getProperties().containsKey("ContainerID")).map(c -> c.getProperty("ContainerID").get().getValue()).collect(Collectors.toList());
        Collection<Container> containersNotInModel = containers.stream().filter(c -> !deploymentModelContainerIds.contains(c.getId())).collect(Collectors.toList());
        return containersNotInModel;
    }
}
