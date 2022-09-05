package org.iac2.service.architecturereconstruction.plugin.implementation.opentoscacontainer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.MysqlDbms;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.component.WebApplication;
import io.github.edmm.model.component.WebServer;
import io.github.edmm.model.support.EdmmYamlBuilder;
import org.iac2.service.architecturereconstruction.common.exception.AppNotFoundException;
import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.model.InstanceModel;
import org.opentosca.container.client.ContainerClient;
import org.opentosca.container.client.ContainerClientBuilder;
import org.opentosca.container.client.model.Application;
import org.opentosca.container.client.model.ApplicationInstance;
import org.opentosca.container.client.model.NodeInstance;
import org.opentosca.container.client.model.RelationInstance;

public class OpenToscaContainerModelCreationPlugin implements ModelCreationPlugin {

    @Override
    public String getIdentifier() {
        return "opentosca-container-model-creation-plugin";
    }

    @Override
    public boolean isIaCTechnologySupported(String iacTechnologyName) {
        return iacTechnologyName.equalsIgnoreCase("opentoscacontainer");
    }

    @Override
    public Collection<String> getRequiredPropertyNames() {
        return List.of(
                "opentoscacontainer_hostname",
                "opentoscacontainer_port",
                "opentoscacontainer_appId",
                "opentoscacontainer_instanceId"
        );
    }

    @Override
    public InstanceModel reconstructInstanceModel(ProductionSystem productionSystem) throws IaCTechnologyNotSupportedException {
        if (!isIaCTechnologySupported(productionSystem.getIacTechnologyName())) {
            throw new IaCTechnologyNotSupportedException(productionSystem.getIacTechnologyName());
        }

        Map<String, String> props = productionSystem.getProperties();

        String hostName = props.get("opentoscacontainer_hostname");
        String port = props.get("opentoscacontainer_port");
        String appId = props.get("opentoscacontainer_appId");
        String instanceId = props.get("opentoscacontainer_instanceId");

        if (Objects.isNull(hostName) || Objects.isNull(port) || Objects.isNull(appId) || Objects.isNull(instanceId)) {
            StringBuilder strb = new StringBuilder();
            strb.append("Missing Properties");
            strb.append(System.lineSeparator());
            strb.append("hostName=").append(hostName);
            strb.append(System.lineSeparator());
            strb.append("port=").append(port);
            strb.append(System.lineSeparator());
            strb.append("appId=").append(appId);
            strb.append(System.lineSeparator());
            strb.append("instanceId=").append(instanceId);

            throw new IllegalArgumentException(strb.toString());
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
            deploymentModel.getComponents().stream().filter(c -> c.getId().equals(n.getId())).toList().forEach(c -> {
                properties.forEach(c::addProperty);
            });
        });

        return new InstanceModel(deploymentModel);
    }

    private Collection<RelationInstance> getRelationInstancesWithSource(ApplicationInstance applicationInstance, String sourceId) {
        return applicationInstance.getRelationInstances().stream().filter(r -> r.getSourceId().equals(sourceId)).collect(Collectors.toList());
    }

    private Class<? extends RootComponent> getClassForNodeInstance(NodeInstance nodeInstance) {
        String type = nodeInstance.getTemplateType();
        // TODO add more later
        switch (type) {
            case "{http://opentosca.org/nodetypes}DockerEngine_w1":
                return Paas.class;
            case "{http://opentosca.org/nodetypes}NGINX_latest-w1":
                return WebServer.class;
            case "{http://opentosca.org/example/applications/nodetypes}RealWorld-Application_Angular-w":
                return WebApplication.class;
            case "{http://opentosca.org/nodetypes}MySQL-DBMS_8.0-w1":
                return MysqlDbms.class;
            default:
                return SoftwareComponent.class;
        }
    }

    private NodeInstance getNodeInstance(ApplicationInstance applicationInstance, String id) {
        return applicationInstance.getNodeInstances().stream().filter(n -> n.getId().equals(id)).findFirst().orElse(null);
    }
}
