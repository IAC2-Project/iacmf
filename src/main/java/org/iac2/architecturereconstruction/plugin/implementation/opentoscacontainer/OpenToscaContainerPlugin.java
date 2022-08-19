package org.iac2.architecturereconstruction.plugin.implementation.opentoscacontainer;

import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.model.DeploymentModel;
import io.swagger.client.api.DefaultApi;
import org.iac2.architecturereconstruction.common.exception.AppNotFoundException;
import org.iac2.architecturereconstruction.common.exception.ArchitectureReconstructionException;
import org.iac2.architecturereconstruction.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.architecturereconstruction.common.exception.InputNotValidException;
import org.iac2.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.SystemModel;
import org.opentosca.container.client.ContainerClient;
import org.opentosca.container.client.ContainerClientBuilder;
import org.opentosca.container.client.model.Application;
import org.opentosca.container.client.model.ApplicationInstance;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class OpenToscaContainerPlugin implements ModelCreationPlugin {

    @Override
    public String getIdentifier() {
        return "opentoscacontainerplugin";
    }

    @Override
    public boolean isIaCTechnologySupported(String iacTechnologyName) {
        return iacTechnologyName.equalsIgnoreCase("opentoscacontainer");
    }

    @Override
    public SystemModel reconstructInstanceModel(ProductionSystem productionSystem) throws IaCTechnologyNotSupportedException {
        if (!isIaCTechnologySupported(productionSystem.getIacTechnologyName())) {
            throw new IaCTechnologyNotSupportedException(productionSystem.getIacTechnologyName());
        }

        Map<String, String> props = productionSystem.getProperties();

        String hostName = props.get("opentoscacontainer_hostname");
        String port = props.get("opentoscacontainer_port");
        String appId = props.get("opentoscacontainer_appId");
        String instanceId = props.get("opentoscacontainer_instanceId");

        if (Objects.isNull(hostName) && Objects.isNull(port) && Objects.isNull(appId) && Objects.isNull(instanceId)) {
            StringBuilder strb = new StringBuilder();
            strb.append("Missing Properties");
            strb.append(System.lineSeparator());
            strb.append("hostName=" + hostName);
            strb.append(System.lineSeparator());
            strb.append("port=" + port);
            strb.append(System.lineSeparator());
            strb.append("appId=" + appId);
            strb.append(System.lineSeparator());
            strb.append("instanceId=" + instanceId);
            throw new InputNotValidException(strb.toString());
        }

        ContainerClient client = ContainerClientBuilder.builder().withHostname(hostName).withPort(Integer.valueOf(port)).build();

        Application app = client.getApplications().stream().filter(a -> a.getId().equals(appId)).findFirst().orElse(null);

        if (Objects.isNull(app)) {
            throw new AppNotFoundException("Couldn't find application with id " + appId);
        }

        ApplicationInstance instance = client.getApplicationInstances(app).stream().filter(i -> i.getId().equals(instanceId)).findFirst().orElse(null);

        if (Objects.isNull(instance)) {
            throw new AppNotFoundException("Couldn't find application instance with id " + instanceId + " of application " + appId);
        }

        SystemModel systemModel = new SystemModel();
        EntityGraph entityGraph = new EntityGraph();

        // this whole thing here feels weird, however, contructing yaml just to read it into this again is a little more weird...
        instance.getNodeInstances().forEach(n -> {
            EntityId id = new EntityId(n.getId());
            Entity entity = new OpenToscaContainerEntity(id, entityGraph);
            entityGraph.addEntity(entity);
        });

        instance.getRelationInstances().forEach(r -> {
            Entity sourceEntity = entityGraph.getEntity(Arrays.asList(r.getSourceId())).get();
            Entity targetEntity = entityGraph.getEntity(Arrays.asList(r.getTargetId())).get();
            EntityGraph.Edge edge = new EntityGraph.Edge(r.getId(), sourceEntity, targetEntity);
            entityGraph.addEdge(sourceEntity, targetEntity, edge);
        });

        DeploymentModel deploymentModel = new DeploymentModel(appId, entityGraph);
        systemModel.setDeploymentModel(deploymentModel);
        return systemModel;
    }
}
