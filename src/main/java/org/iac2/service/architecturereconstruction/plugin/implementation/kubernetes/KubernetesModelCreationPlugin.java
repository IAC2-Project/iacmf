package org.iac2.service.architecturereconstruction.plugin.implementation.kubernetes;

/**
 * The KubernetesModelCreationPlugin class is responsible for retrieving information from a Kubernetes cluster
 * and creating an instance model of a Kubernetes deployment.
 * It implements the ModelCreationPlugin interface.
 */

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.*;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.DependsOn;
import io.github.edmm.model.relation.HostedOn;
import io.github.edmm.model.relation.RootRelation;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import org.assertj.core.util.Sets;
import org.iac2.common.PluginDescriptor;
import org.iac2.common.exception.IaCTechnologyNotSupportedException;
import org.iac2.common.exception.MissingProductionSystemPropertyException;
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.utility.Edmm;
import org.iac2.service.architecturereconstruction.common.exception.AppNotFoundException;
import org.iac2.service.architecturereconstruction.common.exception.NameSpaceNotFoundException;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.plugin.implementation.kubernetes.model.ContainerComponentRelation;
import org.iac2.service.architecturereconstruction.plugin.implementation.kubernetes.model.ContainerInstance;
import org.iac2.service.architecturereconstruction.plugin.implementation.kubernetes.model.RelationType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class KubernetesModelCreationPlugin implements ModelCreationPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesModelCreationPlugin.class);

    /**
     * Strings for defining software components.
     */
    public static final String TOM_CAT = "tomcat";
    public static final String MACHINE = "compute";
    public static final String JBOSS = "jboss";
    public static final String WEBAPP = "WebApplication";
    public static final String MYSQL = "mysql";
    public static final String DB = "db";

    /**
     * Strings for defining component properties.
     */
    public static final String DB_CREDENTIALS = "DB_CREDENTIALS";
    public static final String SECRET = "secret";
    public static final String MACHINE_IMAGE = "MACHINE_IMAGE";

    /**
     * Strings for defining relations.
     */
    public static final String HOSTED_ON = "HostedOn";
    public static final String CONNECTS_TO = "ConnectsTo";

    private final KubernetesModelCreationPluginDescriptor descriptor;

    /**
     * Constructs a new instance of the KubernetesModelCreationPlugin class.
     *
     * @param descriptor the KubernetesModelCreationPluginDescriptor to use
     */
    public KubernetesModelCreationPlugin(KubernetesModelCreationPluginDescriptor descriptor) {
        this.descriptor = descriptor;
    }


    @Override
    public PluginDescriptor getDescriptor() {
        return this.descriptor;
    }


    @Override
    public void setConfigurationEntry(String inputName, String inputValue) {

    }

    @Override
    public String getConfigurationEntry(String name) {
        return null;
    }

    /**
     * Retrieves information from a Kubernetes cluster to create an instance model.
     *
     * @param productionSystem the production system from which to retrieve information
     * @return an instance model representing the retrieved information
     * @throws IaCTechnologyNotSupportedException if the Kubernetes cluster cannot be accessed
     */
    @Override
    public InstanceModel reconstructInstanceModel(ProductionSystem productionSystem)
            throws IaCTechnologyNotSupportedException {
        Map<String, String> props = productionSystem.getProperties();
        String kubeConfigPath = props.get("kubeConfigPath");
        String namespace = props.get("namespace");

        if (Objects.isNull(kubeConfigPath)) {
            throw new MissingProductionSystemPropertyException(productionSystem, "kubeConfigPath");
        } else if (Objects.isNull(namespace)) {
            throw new MissingProductionSystemPropertyException(productionSystem, "namespace");
        }


        try {
            ApiClient client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();

            // set the global default api-client to the in-cluster one from above
            Configuration.setDefaultApiClient(client);

            // the CoreV1Api loads default api-client from global configuration.
            CoreV1Api api = new CoreV1Api(client);

            V1PodList pods = api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null);

            if (Objects.isNull(pods)) {
                throw new NameSpaceNotFoundException("Couldn't find the namespace : " + namespace);
            }

            DeploymentModel deploymentModel = new DeploymentModel(namespace,
                    this.createKubernetesInstanceRelations(pods, namespace));
            return new InstanceModel(deploymentModel);

        } catch (IllegalAccessException | ApiException | IOException e) {
            throw new AppNotFoundException(
                    "Couldn't generate entity graph from referenced application instance", e);
        }

    }

    /**
     * Establishes the relations among various components retrieved from the kubernetes cluster
     *
     * @param pods
     * @param namespace
     * @return EntityGraph
     * @throws IllegalAccessException
     */
    private EntityGraph createKubernetesInstanceRelations(V1PodList pods, String namespace)
            throws IllegalAccessException {
        List<V1Pod> podList = pods.getItems();
        Random random = new Random();

        List<ContainerInstance> containers = new ArrayList<>();
        List<String> instanceTypes = new ArrayList<>();
        List<ContainerComponentRelation> containerRelations = new ArrayList<>();

        // this ensures we add "stray" node instances that are not part of any relation.
        for (V1Pod pod : podList) {

            V1Container container = pod.getSpec().getContainers().get(0);

            Boolean containerAdded = false;
            String imageName = container.getImage();
            String[] imageParts = imageName.split(":");
            String imageWithoutTag = imageParts[0];

            String imageId = pod.getMetadata().getName();

            // Check for database components
            if (!instanceTypes.contains(MYSQL) && !instanceTypes.contains(DB)) {
                if (imageName.contains(MYSQL)) {
                    instanceTypes.add(MYSQL);
                    containers.add(
                            new ContainerInstance(imageId, imageName, MYSQL, namespace, checkContainerEnv(container)));
                    containerAdded = true;
                } else if (imageName.contains(DB)) {
                    instanceTypes.add(DB);
                    containers.add(
                            new ContainerInstance(imageId, imageName, DB, namespace, checkContainerEnv(container)));
                    containerAdded = true;
                }
            } // other database types can be included like Postgress, mongoDB with extension
              // from Database edmm type

            ContainerInstance serverPort = containers.stream()
                    .filter(containerInstance -> containerInstance.getContainerName().equals(TOM_CAT)).findFirst()
                    .orElse(null);

            String serverComponentId = null;

            // Check for web application components
            if (!instanceTypes.contains(TOM_CAT) && !instanceTypes.contains(JBOSS)) {
                if (container.getPorts() != null) {
                    if (!imageName.contains(MYSQL) && !imageName.contains(DB) && !instanceTypes.contains(WEBAPP)) {
                        containers.add(new ContainerInstance(imageId, imageName, WEBAPP, namespace,
                                checkContainerEnv(container)));
                        containerAdded = true;
                        instanceTypes.add(WEBAPP);

                        // Check if the webapp has any connectsTo relationships

                        for (V1ContainerPort port : container.getPorts()) {
                            if (port.getName().equalsIgnoreCase(TOM_CAT)) { // can add another type of component
                                                                            // extending WebServer from edmm
                                serverComponentId = getServerComponentId(port);
                                instanceTypes.add(TOM_CAT);
                                containers.add(
                                        new ContainerInstance(serverComponentId, TOM_CAT, TOM_CAT, namespace, null));
                                containerRelations
                                        .add(new ContainerComponentRelation(imageId, serverComponentId, RelationType.HOSTED_ON));

                            }
                        }
                    }

                }
            }

            // Check the underlying virtual machine for the pod
            if (container.getEnv() != null && containerAdded == true) {
                String machineImage = container.getEnv().stream()
                        .filter(env -> env.getName().equals(MACHINE_IMAGE))
                        .findFirst()
                        .map(env -> env.getValue())
                        .orElse(null);
                if (machineImage != null) {
                    String machineId = machineImage + random.nextInt(1000);
                    containers.add(new ContainerInstance(machineId, machineImage, MACHINE, namespace, null));

                    if (serverComponentId != null) {
                        containerRelations
                                .add(new ContainerComponentRelation(serverComponentId, machineId, RelationType.HOSTED_ON));
                    } else {
                        containerRelations.add(new ContainerComponentRelation(imageId, machineId, RelationType.HOSTED_ON));
                    }
                }
            }
        }

        containerRelations = checkConnectsToRelation(containerRelations, containers, instanceTypes);

        return this.createEntityGraph(containers, containerRelations);
    }

    /**
     * Checking for environment variables with prefix DB_ to check the database credentials type
     *
     * @param container
     * @return List<V1EnvVar>
     */
    @NotNull
    private static List<V1EnvVar> checkContainerEnv(V1Container container) {
        List<V1EnvVar> containerEnvVar = container.getEnv();
        for (V1EnvVar env : containerEnvVar) {
            if (env.getName().startsWith("DB_")) {
                if (env.getValueFrom().getSecretKeyRef() != null) {
                    V1EnvVar dbCred = new V1EnvVar();
                    dbCred.setName(DB_CREDENTIALS);
                    dbCred.setValue(SECRET);
                    containerEnvVar.add(dbCred);
                    break;
                }
            }
        }
        return containerEnvVar;
    }

    /**
     * Established connects to relation among different components
     *
     * @param containerRelations
     * @param containers
     * @param instanceTypes
     * @return List<ContainerComponentRelations>
     */
    private List<ContainerComponentRelation> checkConnectsToRelation(
            List<ContainerComponentRelation> containerRelations, List<ContainerInstance> containers,
            List<String> instanceTypes) {

        if (instanceTypes.contains(WEBAPP)) {
            ContainerInstance sourceComp = containers.stream().filter(e -> e.getContainerType().equals(WEBAPP))
                    .findFirst().orElse(null);

            List<V1EnvVar> envVars = sourceComp.getContainerEnvVar();
            ContainerInstance targetComp = null;
            for (V1EnvVar env : envVars) {
                if (env.getName().startsWith("DB_")) {
                    targetComp = containers.stream()
                            .filter(e -> e.getContainerType().equals(MYSQL) || e.getContainerType().equals(DB))
                            .findFirst().orElse(null);
                    if (targetComp != null) {
                        containerRelations.add(new ContainerComponentRelation(sourceComp.getContainerId(),
                                targetComp.getContainerId(), RelationType.CONNECTS_TO));
                        break;
                    }
                }
            }
        }
        return containerRelations;
    }

    /**
     *
     * @param containers
     * @param containerRelations
     * @return EntityGraph
     * @throws IllegalAccessException
     */
    private EntityGraph createEntityGraph(List<ContainerInstance> containers,
            List<ContainerComponentRelation> containerRelations) throws IllegalAccessException {
        EntityGraph entityGraph = new EntityGraph();
        Collection<EntityId> compIds = Sets.newHashSet();

        for (ContainerInstance containerInstance : containers) {
            EntityId currentId = addNodeInstanceAsComp(entityGraph, containerInstance);
            compIds.add(currentId);
            LOGGER.info("added '{}' to the graph. Container instance id: {}. Container instance template id: {}",
                    currentId,
                    containerInstance.getContainerId(),
                    containerInstance.getContainerType());
        }

        for (ContainerComponentRelation containerComponentRelation : containerRelations) {
            EntityId sourceEntityId = getEntityId(compIds, containerComponentRelation.getSourceId());
            EntityId targetEntityId = getEntityId(compIds, containerComponentRelation.getTargetId());

            if (sourceEntityId != null && targetEntityId != null) {
                Edmm.addRelation(entityGraph, sourceEntityId, targetEntityId,
                        getRelationClass(containerComponentRelation));
            }
        }
        return entityGraph;
    }

    private static EntityId getEntityId(Collection<EntityId> entityIds, String containerId) {
        return entityIds.stream().filter(e -> e.getName().equals(containerId)).findFirst().orElse(null);
    }

    private String getServerComponentId(V1ContainerPort port) {
        return port.getContainerPort() + port.getName(); // check this
    }

    private static EntityId addNodeInstanceAsComp(EntityGraph entityGraph, ContainerInstance containerInstance)
            throws IllegalAccessException {
        List<V1EnvVar> envVars = containerInstance.getContainerEnvVar();
        Map<String, Object> properties = new HashMap<>();

        if (envVars != null) {
            for (V1EnvVar env : envVars) {
                properties.put(env.getName(), env.getValue());
            }
        }
        return Edmm.addComponent(entityGraph, containerInstance.getContainerId(), properties,
                getClassForTemplateId(containerInstance.getContainerType()));
    }

    private static Class<? extends RootComponent> getClassForTemplateId(String templateType) {
        return switch (QName.valueOf(templateType).getLocalPart()) {
            case MYSQL -> MysqlDatabase.class;
            case TOM_CAT -> Tomcat.class;
            case MACHINE -> Compute.class;
            case WEBAPP -> WebApplication.class;
            case DB -> Database.class;
            default -> SoftwareComponent.class;
        };
    }

    private static Class<? extends RootRelation> getRelationClass(ContainerComponentRelation relationInstance) {

        return switch (relationInstance.getRelationType()) {
            case HOSTED_ON -> HostedOn.class;
            case CONNECTS_TO -> ConnectsTo.class;
        };
    }
}
