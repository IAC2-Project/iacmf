package org.iac2.architecturereconstruction.plugin.implementation.opentoscacontainer;

import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.*;
import io.github.edmm.model.support.EdmmYamlBuilder;
import io.swagger.client.api.DefaultApi;
import org.iac2.architecturereconstruction.common.exception.AppNotFoundException;
import org.iac2.architecturereconstruction.common.exception.ArchitectureReconstructionException;
import org.iac2.architecturereconstruction.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.architecturereconstruction.common.exception.InputNotValidException;
import org.iac2.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.SystemModel;
import org.jgrapht.graph.builder.GraphBuilder;
import org.opentosca.container.client.ContainerClient;
import org.opentosca.container.client.ContainerClientBuilder;
import org.opentosca.container.client.model.Application;
import org.opentosca.container.client.model.ApplicationInstance;
import org.opentosca.container.client.model.NodeInstance;
import org.opentosca.container.client.model.RelationInstance;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        // Edit: it is even funnier now
        EdmmYamlBuilder yamlBuilder = new EdmmYamlBuilder();

        instance.getNodeInstances().forEach(n -> {
            yamlBuilder.component(this.getClassForNodeInstance(n), n.getId());
            this.getRelationInstancesWithSource(instance, n.getId()).forEach(r ->
            {
                NodeInstance targetInstance = this.getNodeInstance(instance, r.getTargetId());
                switch (r.getTemplateType()) {
                    case "{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}HostedOn":
                        yamlBuilder.hostedOn(this.getClassForNodeInstance(targetInstance), targetInstance.getId());
                        break;
                    case "{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}ConnectsTo":
                        yamlBuilder.connectsTo(this.getClassForNodeInstance(targetInstance), targetInstance.getId());
                        break;
                }
            });
        });

        String yamlString = yamlBuilder.build();

        DeploymentModel deploymentModel = DeploymentModel.of(yamlString);

        instance.getNodeInstances().forEach(n -> {
            Map<String, String> properties = n.getProperties();
            deploymentModel.getComponents().stream().filter(c -> c.getId().equals(n.getId())).collect(Collectors.toList()).forEach(c -> {
                properties.forEach((k,v) -> {
                    c.addProperty(k,v);
                });
            });
        });

        systemModel.setDeploymentModel(deploymentModel);
        return systemModel;
    }

    private Collection<RelationInstance> getRelationInstancesWithSource(ApplicationInstance applicationInstance, String sourceId) {
        return applicationInstance.getRelationInstances().stream().filter(r -> r.getSourceId().equals(sourceId)).collect(Collectors.toList());
    }

    private Class<? extends RootComponent> getClassForNodeInstance(NodeInstance nodeInstance) {
        String type = nodeInstance.getTemplateType();
        // TODO add more later
        if (type.equals("{http://opentosca.org/nodetypes}DockerEngine_w1")) {
            return Paas.class;
        } else if (type.equals("{http://opentosca.org/nodetypes}NGINX_latest-w1")) {
            return WebServer.class;
        } else if (type.equals("{http://opentosca.org/example/applications/nodetypes}RealWorld-Application_Angular-w1")) {
            return WebApplication.class;
        } else if (type.equals("{http://opentosca.org/nodetypes}MySQL-DBMS_8.0-w1")) {
            return MysqlDbms.class;
        } else {
            return SoftwareComponent.class;
        }
    }

    private NodeInstance getNodeInstance(ApplicationInstance applicationInstance, String id) {
        return applicationInstance.getNodeInstances().stream().filter(n -> n.getId().equals(id)).findFirst().orElse(null);
    }
}
