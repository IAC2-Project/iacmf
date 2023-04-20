package org.iac2.service.architecturereconstruction.plugin.implementation.kubernetes;

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
import org.iac2.common.model.InstanceModel;
import org.iac2.common.model.ProductionSystem;
import org.iac2.common.utility.Edmm;
import org.iac2.service.architecturereconstruction.common.exception.NameSpaceNotFoundException;
import org.iac2.service.architecturereconstruction.common.interfaces.ModelCreationPlugin;
import org.iac2.service.architecturereconstruction.common.model.Kubernetes.ContainerComponentRelations;
import org.iac2.service.architecturereconstruction.common.model.Kubernetes.ContainerInstance;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class KubernetesModelCreationPlugin implements ModelCreationPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesModelCreationPlugin.class);


    public static final String TOM_CAT = "tomcat";
    public static final String MACHINE = "compute";
    public static final String JBOSS = "jboss";
    public static final String WEBAPP = "WebApplication";
    public static final String MYSQL = "mysql";
    public static final String DB = "db";

    public static final String DB_CREDENTIALS = "DB_CREDENTIALS";

    public static final String SECRET = "secret";
    public static final String MACHINE_IMAGE = "MACHINE_IMAGE";
    public static final String HOSTED_ON = "HostedOn";
    public static final String CONNECTS_TO = "ConnectsTo";

    private final KubernetesModelCreationPluginDescriptor descriptor;

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


    @Override
    public InstanceModel reconstructInstanceModel(ProductionSystem productionSystem) throws IaCTechnologyNotSupportedException {

        Map<String, String> props = productionSystem.getProperties();

        String kubeConfigPath = props.get("kubeConfigPath");
        String namespace = props.get("namespace");

        ApiClient client = null;

        if (Objects.isNull(kubeConfigPath) || Objects.isNull(namespace) ) {
            String strb = "Missing Properties" +
                    System.lineSeparator() +
                    "kubeConfigPath=" + kubeConfigPath +
                    System.lineSeparator() +
                    "namespace=" + namespace;

            throw new IllegalArgumentException(strb);
        }

        try {
            client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();


        // set the global default api-client to the in-cluster one from above
        Configuration.setDefaultApiClient(client);

        // the CoreV1Api loads default api-client from global configuration.
        CoreV1Api api = new CoreV1Api(client);

        V1PodList pods = api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null,null);


        if(Objects.isNull(pods)){
            throw new NameSpaceNotFoundException("Couldn't find the namespace : " + namespace);
        }

        DeploymentModel deploymentModel = new DeploymentModel(namespace, this.createKubernetesInstanceRelations(pods,namespace));
        return new InstanceModel(deploymentModel);

        } catch (IllegalAccessException | ApiException | FileNotFoundException e ) {
            throw new IaCTechnologyNotSupportedException("Couldn't generate entity graph from referenced application instance", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private EntityGraph createKubernetesInstanceRelations(V1PodList pods, String namespace) throws IllegalAccessException {
        List<V1Pod> podList = pods.getItems();
        Random random = new Random();


        List<ContainerInstance> containers = new ArrayList<>();
        List<String> instanceTypes = new ArrayList<>();
        List<ContainerComponentRelations> containerRelations = new ArrayList<>();

        // this ensures we add "stray" node instances that are not part of any relation.
        for (V1Pod pod : podList) {

            V1Container container = pod.getSpec().getContainers().get(0);

            Boolean containerAdded = false;
            String imageName = container.getImage();
            String[] imageParts = imageName.split(":");
            String imageWithoutTag = imageParts[0];

            String imageId = pod.getMetadata().getName();


            //Check for database components
            if( !instanceTypes.contains(MYSQL) && !instanceTypes.contains(DB) ) {
                if (imageName.contains(MYSQL)) {
                    instanceTypes.add(MYSQL);
                    containers.add(new ContainerInstance(imageId,imageName,MYSQL,namespace,checkContainerEnv(container)));
                    containerAdded = true;
                }else if(imageName.contains(DB)) {
                    instanceTypes.add(DB);
                    containers.add(new ContainerInstance(imageId,imageName,DB,namespace,checkContainerEnv(container)));
                    containerAdded = true;
                }
            } //other database types can be included like Postgress, mongoDB with extension from Database edmm type


            ContainerInstance serverPort = containers.stream().filter(containerInstance -> containerInstance.getContainerName().equals(TOM_CAT)).findFirst().orElse(null);

            String serverComponentId = null;

            //Check for web application components
            if( !instanceTypes.contains(TOM_CAT) && !instanceTypes.contains(JBOSS)) {
                if (container.getPorts() != null) {
                    if(!imageName.contains(MYSQL) && !imageName.contains(DB) && !instanceTypes.contains(WEBAPP)) {//add a list of possible ports apart from main application component
                        containers.add(new ContainerInstance(imageId,imageName,WEBAPP,namespace,checkContainerEnv(container)));
                        containerAdded = true;
                        instanceTypes.add(WEBAPP);

                        //Check if the webapp has any connectsTo relationships

                        for (V1ContainerPort port : container.getPorts()) {
                            if (port.getName().equalsIgnoreCase(TOM_CAT)) {  // can add another type of component extending WebServer from edmm
                                serverComponentId = getServerComponentId(port);
                                instanceTypes.add(TOM_CAT);
                                containers.add(new ContainerInstance(serverComponentId,TOM_CAT,TOM_CAT,namespace,null));
                                containerRelations.add(new ContainerComponentRelations(imageId,serverComponentId,HOSTED_ON));

                            }
                        }
                    }

                }
            }

            //Check the underlying virtual machine for the pod
            if (container.getEnv() != null && containerAdded == true) {
                String machineImage = container.getEnv().stream()
                        .filter(env -> env.getName().equals(MACHINE_IMAGE))
                       .findFirst()
                       .map(env -> env.getValue())
                       .orElse(null);
                if(machineImage != null){
                String machineId = machineImage+random.nextInt(1000);
                containers.add(new ContainerInstance(machineId,machineImage,MACHINE,namespace, null));
                
                if(serverComponentId != null){
                    containerRelations.add(new ContainerComponentRelations(serverComponentId,machineId,HOSTED_ON));
                }else{
                    containerRelations.add(new ContainerComponentRelations(imageId,machineId,HOSTED_ON));
                }
                }
            }
        }

        containerRelations = checkConnectsToRelation(containerRelations,containers, instanceTypes);

        return this.createEntityGraph(containers, containerRelations);
    }

    @NotNull
    private static List<V1EnvVar> checkContainerEnv(V1Container container) {
        List<V1EnvVar> containerEnvVar = container.getEnv();
        for(V1EnvVar env : containerEnvVar){
            if(env.getName().startsWith("DB_")){
                if(env.getValueFrom().getSecretKeyRef() != null){
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

    private List<ContainerComponentRelations> checkConnectsToRelation(List<ContainerComponentRelations> containerRelations, List<ContainerInstance> containers, List<String> instanceTypes) {

        if(instanceTypes.contains(WEBAPP)){
           ContainerInstance sourceComp =  containers.stream().filter(e -> e.getContainerType().equals(WEBAPP)).findFirst().orElse(null);

           List<V1EnvVar> envVars = sourceComp.getContainerEnvVar();
            ContainerInstance targetComp = null;
           for(V1EnvVar env : envVars){
               if(env.getName().startsWith("DB_")){
                    targetComp =  containers.stream().filter(e -> e.getContainerType().equals(MYSQL) || e.getContainerType().equals(DB)).findFirst().orElse(null);
                   if(targetComp!=null){
                       containerRelations.add(new ContainerComponentRelations(sourceComp.getContainerId(),targetComp.getContainerId(),CONNECTS_TO));
                       break;
                   }
               }
           }
        }
        return containerRelations;
    }

    private EntityGraph createEntityGraph(List<ContainerInstance> containers, List<ContainerComponentRelations> containerRelations) throws IllegalAccessException {
        EntityGraph entityGraph = new EntityGraph();
        Collection<EntityId> compIds = Sets.newHashSet();

        for (ContainerInstance containerInstance: containers){
            EntityId currentId = addNodeInstanceAsComp(entityGraph, containerInstance);
            compIds.add(currentId);
            LOGGER.info("added '{}' to the graph. Container instance id: {}. Container instance template id: {}",
                    currentId,
                    containerInstance.getContainerId(),
                    containerInstance.getContainerType());
        }

        for (ContainerComponentRelations containerComponentRelation : containerRelations) {
            EntityId sourceEntityId = getEntityId(compIds, containerComponentRelation.getSourceId());
            EntityId targetEntityId = getEntityId(compIds, containerComponentRelation.getTargetId());

            if(sourceEntityId!=null && targetEntityId!=null) {
                Edmm.addRelation(entityGraph, sourceEntityId, targetEntityId, getRelationClass(containerComponentRelation));
            }
        }
        return entityGraph;
    }

    private static EntityId getEntityId(Collection<EntityId> entityIds, String containerId) {
        return entityIds.stream().filter(e -> e.getName().equals(containerId)).findFirst().orElse(null);
    }

    private String getServerComponentId(V1ContainerPort port) {
        return port.getContainerPort()+port.getName();    // check this
    }

    private static EntityId addNodeInstanceAsComp(EntityGraph entityGraph, ContainerInstance containerInstance) throws IllegalAccessException {
        List<V1EnvVar> envVars = containerInstance.getContainerEnvVar();
        Map<String, Object> properties = new HashMap<>();

        if(envVars!=null) {
        for(V1EnvVar env : envVars){
                properties.put(env.getName(),env.getValue());
        }
        }
        return Edmm.addComponent(entityGraph, containerInstance.getContainerId(), properties, getClassForTemplateId(containerInstance.getContainerType()));
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

    private static Class<? extends RootRelation> getRelationClass(ContainerComponentRelations relationInstance) {

        return switch (relationInstance.getRelationType()) {
            case HOSTED_ON -> HostedOn.class;
            case CONNECTS_TO -> ConnectsTo.class;
            default -> DependsOn.class;
        };
    }
}
