package org.iac2.service.architecturereconstruction.plugin.implementation.opentoscacontainer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.DependsOn;
import io.github.edmm.model.relation.HostedOn;
import io.github.edmm.model.relation.RootRelation;
import org.assertj.core.util.Sets;
import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.utility.Edmm;
import org.iac2.service.architecturereconstruction.common.exception.AppInstanceNodeFoundException;
import org.iac2.service.architecturereconstruction.common.exception.AppNotFoundException;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerContainer;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerEngine;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.Java11;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.MySqlDb;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.MySqlDbms;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.Nginx;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.RealWorldAngularApp;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.RealWorldApplicationBackendJava11Spring;
import org.opentosca.container.client.ContainerClient;
import org.opentosca.container.client.ContainerClientBuilder;
import org.opentosca.container.client.model.Application;
import org.opentosca.container.client.model.ApplicationInstance;
import org.opentosca.container.client.model.NodeInstance;
import org.opentosca.container.client.model.RelationInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenToscaContainerModelCreationPlugin implements ModelCreationPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenToscaContainerModelCreationPlugin.class);
    private final static int OPENTOSCA_CLIENT_TIMEOUT = 10000;

    public OpenToscaContainerModelCreationPlugin() {
    }

    private static Class<? extends RootRelation> getRelationClass(RelationInstance relationInstance) {

        return switch (relationInstance.getTemplateType()) {
            case "{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}HostedOn" -> HostedOn.class;
            case "{http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes}ConnectsTo" -> ConnectsTo.class;
            default -> DependsOn.class;
        };
    }

    private static EntityId addNodeInstanceAsComp(EntityGraph entityGraph, NodeInstance nodeInstance) throws IllegalAccessException {
        Map<String, Object> properties = new HashMap<>(nodeInstance.getProperties());
        return Edmm.addComponent(entityGraph, nodeInstance.getTemplate(), properties, getClassForTemplateId(nodeInstance.getTemplateType()));
    }

    private static Class<? extends RootComponent> getClassForTemplateId(String templateType) {
        return switch (QName.valueOf(templateType).getLocalPart()) {
            case "MySQL-DBMS_8.0-w1" -> MySqlDbms.class;
            case "MySQL-DB_w1" -> MySqlDb.class;
            case "RealWorld-Application-Backend_Java11-Spring-w1" -> RealWorldApplicationBackendJava11Spring.class;
            case "Java_11-w1" -> Java11.class;
            case "RealWorld-Application_Angular-w1" -> RealWorldAngularApp.class;
            case "NGINX_latest-w1" -> Nginx.class;
            case "DockerContainer_w1" -> DockerContainer.class;
            case "DockerEngine_w1" -> DockerEngine.class;
            default -> SoftwareComponent.class;
        };
    }

    private static EntityId getEntityId(Collection<EntityId> entityIds, NodeInstance instance) {
        return entityIds.stream().filter(e -> e.getName().equals(instance.getTemplate())).findFirst().orElse(null);
    }

    private static NodeInstance findNodeInstanceByNodeInstanceId(Collection<NodeInstance> nodeInstances, String nodeInstanceId) {
        return nodeInstances.stream()
                .filter(n -> n.getId().equals(nodeInstanceId)).findFirst().orElseThrow(AppInstanceNodeFoundException::new);
    }

    private static NodeInstance findNodeInstanceByNodeInstanceId(ApplicationInstance applicationInstance, String nodeInstanceId) {
        return findNodeInstanceByNodeInstanceId(applicationInstance.getNodeInstances(), nodeInstanceId);
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
    public Collection<String> getRequiredConfigurationEntryNames() {
        return Collections.emptyList();
    }

    @Override
    public void setConfigurationEntry(String inputName, String inputValue) {
        LOGGER.warn("Trying to pass user input to a plugin that does not need user inputs!");
    }

    @Override
    public String getConfigurationEntry(String name) {
        LOGGER.warn("Trying to get user input from a plugin that does not have user inputs!");
        return null;
    }

    @Override
    public Collection<String> getRequiredProductionSystemPropertyNames() {
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

        ContainerClient client = ContainerClientBuilder.builder().withHostname(hostName).withPort(Integer.valueOf(port)).withTimeout(OPENTOSCA_CLIENT_TIMEOUT, TimeUnit.MILLISECONDS).build();

        Application app = client.getApplications().stream().filter(a -> a.getId().equals(appId)).findFirst().orElse(null);

        if (Objects.isNull(app)) {
            throw new AppNotFoundException("Couldn't find application with id " + appId);
        }

        ApplicationInstance instance = client.getApplicationInstances(app)
                .stream()
                .filter(i -> i.getId().equals(instanceId))
                .findFirst()
                .orElse(null);

        if (Objects.isNull(instance)) {
            throw new AppNotFoundException("Couldn't find application instance with id " + instanceId + " of application " + appId);
        }

        try {
            DeploymentModel deploymentModel = new DeploymentModel(app.getName(), this.createEntityGraph(instance));
            return new InstanceModel(deploymentModel);
        } catch (IllegalAccessException e) {
            throw new IaCTechnologyNotSupportedException("Couldn't generate entity graph from referenced application instance", e);
        }
    }

    private EntityGraph createEntityGraph(ApplicationInstance applicationInstance) throws IllegalAccessException {
        EntityGraph entityGraph = new EntityGraph();
        Collection<EntityId> compIds = Sets.newHashSet();

        // this ensures we add "stray" node instances that are not part of any relation.
        for (NodeInstance instance : applicationInstance.getNodeInstances()) {
            EntityId currentId = addNodeInstanceAsComp(entityGraph, instance);
            LOGGER.info("added '{}' to the graph. Node instance id: {}. Node instance template id: {}",
                    currentId,
                    instance.getId(),
                    instance.getTemplate());
            compIds.add(currentId);
        }

        for (RelationInstance relationInstance : applicationInstance.getRelationInstances()) {
            NodeInstance sourceInstance = findNodeInstanceByNodeInstanceId(applicationInstance, relationInstance.getSourceId());
            NodeInstance targetInstance = findNodeInstanceByNodeInstanceId(applicationInstance, relationInstance.getTargetId());
            EntityId sourceEntityId = getEntityId(compIds, sourceInstance);
            EntityId targetEntityId = getEntityId(compIds, targetInstance);

            assert sourceEntityId != null;
            assert targetEntityId != null;

            Edmm.addRelation(entityGraph, sourceEntityId, targetEntityId, getRelationClass(relationInstance));
        }

        return entityGraph;
    }
}
