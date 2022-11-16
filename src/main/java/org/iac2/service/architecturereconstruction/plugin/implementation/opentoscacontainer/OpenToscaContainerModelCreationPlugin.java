package org.iac2.service.architecturereconstruction.plugin.implementation.opentoscacontainer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.MysqlDbms;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.component.WebApplication;
import io.github.edmm.model.component.WebServer;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.DependsOn;
import io.github.edmm.model.relation.HostedOn;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.support.EdmmYamlBuilder;
import org.assertj.core.util.Sets;
import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.utility.Edmm;
import org.iac2.common.utility.EdmmTypeResolver;
import org.iac2.service.architecturereconstruction.common.exception.AppInstanceNodeFoundException;
import org.iac2.service.architecturereconstruction.common.exception.AppNotFoundException;
import org.iac2.service.architecturereconstruction.common.exception.ArchitectureReconstructionException;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.*;
import org.opentosca.container.client.ContainerClient;
import org.opentosca.container.client.ContainerClientBuilder;
import org.opentosca.container.client.model.Application;
import org.opentosca.container.client.model.ApplicationInstance;
import org.opentosca.container.client.model.NodeInstance;
import org.opentosca.container.client.model.RelationInstance;

import javax.xml.namespace.QName;

public class OpenToscaContainerModelCreationPlugin implements ModelCreationPlugin {

    public OpenToscaContainerModelCreationPlugin() {
        EdmmTypeResolver.putMapping("docker_engine", DockerEngine.class);
        EdmmTypeResolver.putMapping("docker_container", DockerContainer.class);

        // TODO clean this up later
        EdmmTypeResolver.putMapping("MySQL-DBMS_8.0-w1", MySqlDbms.class);
        EdmmTypeResolver.putMapping("MySQL-DB_w1", MySqlDb.class);
        EdmmTypeResolver.putMapping("RealWorld-Application-Backend_Java11-Spring-w1", RealWorldApplicationBackendJava11Spring.class);
        EdmmTypeResolver.putMapping("Java_11-w1", Java11.class);
        EdmmTypeResolver.putMapping("RealWorld-Application_Angular-w1", RealWorldAngularApp.class);
        EdmmTypeResolver.putMapping("NGINX_latest-w1", Nginx.class);
    }

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

        DeploymentModel deploymentModel = null;
        try {
            deploymentModel = new DeploymentModel(app.getName(), this.createEntityGraph(instance));
        } catch (IllegalAccessException e) {
            throw new IaCTechnologyNotSupportedException("Couldn't generate entity graph from referenced application instance", e);
        }

        return new InstanceModel(deploymentModel);
    }

    private EntityGraph createEntityGraph(ApplicationInstance applicationInstance) throws IllegalAccessException {
        EntityGraph entityGraph = new EntityGraph();
        Collection<EntityId> compIds = Sets.newHashSet();

        for (RelationInstance relationInstance : applicationInstance.getRelationInstances()) {
            NodeInstance sourceInstance = this.getNodeInstance(applicationInstance, relationInstance.getSourceId());
            NodeInstance targetInstance = this.getNodeInstance(applicationInstance, relationInstance.getTargetId());
            EntityId sourceEntityId = this.getEntityId(compIds, sourceInstance);
            EntityId targetEntityId = this.getEntityId(compIds, targetInstance);

            if (sourceEntityId == null) {
                sourceEntityId = this.addNodeInstanceAsComp(entityGraph, sourceInstance);
                compIds.add(sourceEntityId);
            }

            if (targetEntityId == null) {
                targetEntityId = this.addNodeInstanceAsComp(entityGraph, targetInstance);
                compIds.add(targetEntityId);
            }

            Edmm.addRelation(entityGraph, sourceEntityId, targetEntityId, this.getRelationClass(relationInstance));
        }

        return entityGraph;
    }

    private Class<? extends RootRelation> getRelationClass(RelationInstance relationInstance) {
        switch (relationInstance.getTemplateType()) {
            case "{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}HostedOn":
               return HostedOn.class;
            case "{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}ConnectsTo":
                return ConnectsTo.class;
            default:
                return DependsOn.class;
        }
    }

    private EntityId addNodeInstanceAsComp(EntityGraph entityGraph, NodeInstance nodeInstance) throws IllegalAccessException {
        return Edmm.addComponent(entityGraph, nodeInstance.getTemplate(),nodeInstance.getProperties(), this.getClassForTemplateId(nodeInstance.getTemplateType()));
    }

    private Class<? extends RootComponent> getClassForTemplateId(String templateType) {
        switch (QName.valueOf(templateType).getLocalPart()) {
            case "MySQL-DBMS_8.0-w1": return MySqlDbms.class;
            case "MySQL-DB_w1": return MySqlDb.class;
            case "RealWorld-Application-Backend_Java11-Spring-w1": return RealWorldApplicationBackendJava11Spring.class;
            case "Java_11-w1": return Java11.class;
            case "RealWorld-Application_Angular-w1": return RealWorldAngularApp.class;
            case "NGINX_latest-w1": return Nginx.class;
            case "DockerContainer_w1": return DockerContainer.class;
            case "DockerEngine_w1": return DockerEngine.class;
            default: return SoftwareComponent.class;
        }
    }

    private EntityId getEntityId(Collection<EntityId> entityIds, NodeInstance instance) {
        return entityIds.stream().filter(e -> e.getName().equals(instance.getTemplate())).findFirst().orElse(null);
    }

    private NodeInstance getNodeInstance(Collection<NodeInstance> nodeInstances, String id) {
        return nodeInstances.stream()
                .filter(n -> n.getId().equals(id)).findFirst().orElseThrow(AppInstanceNodeFoundException::new);
    }

    private NodeInstance getNodeInstance(ApplicationInstance applicationInstance, String id) {
       return this.getNodeInstance(applicationInstance.getNodeInstances(), id);
    }
}
