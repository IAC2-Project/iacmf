package org.iac2.service.architecturereconstruction.plugin.implementation.opentoscacontainer;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.support.EdmmYamlBuilder;
import org.apache.commons.compress.utils.Lists;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelEnhancementPlugin;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DockerContainerReconstructionPlugin implements ModelEnhancementPlugin {
    @Override
    public Collection<String> getRequiredPropertyNames() {
        Collection<String> reqs = Lists.newArrayList();
        reqs.add("DockerEngineUrl");
        return reqs;
    }

    @Override
    public String getIdentifier() {
        return "dockerContainerReconstructionPlugin";
    }

    @Override
    public void enhanceModel(InstanceModel instanceModel, ProductionSystem productionSystem) {

        this.getDockerEngineComponents(instanceModel.getDeploymentModel()).forEach(d -> {
            String dockerEngineUrl = d.getProperty("DockerEngineURL").get().getValue();
            DefaultDockerClientConfig dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                    .withDockerHost(dockerEngineUrl)
                    .withDockerTlsVerify(false)
                    .build();

            DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                    .dockerHost(dockerConfig.getDockerHost())
                    .sslConfig(dockerConfig.getSSLConfig())
                    .maxConnections(100)
                    .connectionTimeout(Duration.ofSeconds(30))
                    .responseTimeout(Duration.ofSeconds(45))
                    .build();

            DockerClient dockerClient = DockerClientImpl.getInstance(dockerConfig, httpClient);

            List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();

            Collection<Container> containersNotInModel = this.findContainersNotInDeploymentModel(instanceModel.getDeploymentModel(), containers);

            containersNotInModel.forEach(c -> this.addDockerContainerToDeploymentModel(instanceModel.getDeploymentModel(), d, c));
        });
    }

    private void addDockerContainerToDeploymentModel(DeploymentModel deploymentModel, RootComponent dockerEngineComponent, Container dockerContainer) {

    }

    private Collection<RootComponent> getDockerEngineComponents(DeploymentModel deploymentModel) {
        return deploymentModel.getComponents().stream().filter(c -> c.getProperties().containsKey("DockerEngineURL")).collect(Collectors.toList());
    }

    private Collection<Container> findContainersNotInDeploymentModel(DeploymentModel deploymentModel, Collection<Container> containers) {
        Collection<String> deploymentModelContainerIds = deploymentModel.getComponents().stream().filter(c -> c.getProperties().containsKey("ContainerID")).map(c -> c.getProperty("ContainerID").get().getValue()).collect(Collectors.toList());
        Collection<Container> containersNotInModel = containers.stream().filter(c -> deploymentModelContainerIds.contains(c.getId())).collect(Collectors.toList());
        return containersNotInModel;
    }
}
