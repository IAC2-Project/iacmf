package org.iac2.service.utility;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.core.parser.ScalarEntity;
import io.github.edmm.core.parser.support.DefaultKeys;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.Property;
import io.github.edmm.model.component.AwsBeanstalk;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.MysqlDatabase;
import io.github.edmm.model.component.MysqlDbms;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.Platform;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.DependsOn;
import io.github.edmm.model.relation.HostedOn;
import org.iac2.common.utility.Edmm;
import org.iac2.common.utility.EdmmTypeResolver;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerContainer;
import org.iac2.service.architecturereconstruction.common.model.EdmmTypes.DockerEngine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

class EdmmTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(EdmmTest.class);

    @BeforeAll
    static void init() {
        EdmmTypeResolver.initDefaultMappings();
    }

    @Test
    void testAddComponentType() throws IOException, IllegalAccessException {
        ClassPathResource resource = new ClassPathResource("edmm/four-components-hosted-on.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        Edmm.addType(model.getGraph(), AwsBeanstalk.class, true);
        Assertions.assertTrue(
                model.getGraph().getEntity(EntityGraph.COMPONENT_TYPES.extend(EdmmTypeResolver.resolve(AwsBeanstalk.class))).isPresent());
        Assertions.assertTrue(
                model.getGraph().getEntity(EntityGraph.COMPONENT_TYPES.extend(EdmmTypeResolver.resolve(Paas.class))).isPresent());
        Assertions.assertTrue(
                model.getGraph().getEntity(EntityGraph.COMPONENT_TYPES.extend(EdmmTypeResolver.resolve(Platform.class))).isPresent());
        Assertions.assertTrue(
                model.getGraph().getEntity(EntityGraph.COMPONENT_TYPES.extend(EdmmTypeResolver.resolve(RootComponent.class))).isPresent());
    }

    @Test
    void testAddRelationType() throws IOException, IllegalAccessException {
        EntityGraph graph = new EntityGraph();
        Edmm.addType(graph, HostedOn.class, false);
        Assertions.assertTrue(
                graph.getEntity(EntityGraph.RELATION_TYPES.extend(EdmmTypeResolver.resolve(HostedOn.class))).isPresent());
        Assertions.assertTrue(
                graph.getEntity(EntityGraph.RELATION_TYPES.extend(EdmmTypeResolver.resolve(DependsOn.class))).isPresent());
    }

    @Test
    void testAddRelation() throws IOException, IllegalAccessException {
        ClassPathResource resource = new ClassPathResource("edmm/four-components-hosted-on.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        EntityId source = EntityGraph.COMPONENTS.extend("windows");
        EntityId target = EntityGraph.COMPONENTS.extend("ubuntu");
        Edmm.addRelation(model.getGraph(), source, target, ConnectsTo.class);
        Assertions.assertTrue(
                model.getGraph().getEntity(EntityGraph.RELATION_TYPES.extend(EdmmTypeResolver.resolve(ConnectsTo.class))).isPresent());
        Assertions.assertTrue(
                model.getGraph().getEntity(EntityGraph.RELATION_TYPES.extend(EdmmTypeResolver.resolve(DependsOn.class))).isPresent());
        Assertions.assertTrue(model.getGraph().getEntity(EntityGraph.COMPONENTS.extend("windows").extend(DefaultKeys.RELATIONS))
                .isPresent());

        model = new DeploymentModel(model.getName(), model.getGraph());
        Assertions.assertEquals(3,
                model.getRelations().stream().filter(r -> r.getTarget().equals(target.getName())).toList().size());
        Assertions.assertEquals(1, model.getComponent(source.getName()).orElseThrow().getRelations().size());
        Assertions.assertEquals(0, model.getComponent(target.getName()).orElseThrow().getRelations().size());
    }

    @Test
    void testAddComponent() throws IOException, IllegalAccessException {
        ClassPathResource resource = new ClassPathResource("edmm/four-components-hosted-on.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        EntityId id = Edmm.addComponent(model.getGraph(), "flip-flop-machine", Map.of("os_family", "flip-flop-os"), Compute.class);
        Assertions.assertEquals("flip-flop-machine", id.getName());
        Optional<Entity> properties = model.getGraph().getEntity(id.extend(DefaultKeys.PROPERTIES));
        Assertions.assertTrue(properties.isPresent());
        Assertions.assertTrue(properties.get() instanceof MappingEntity);
        Optional<Entity> property = model.getGraph().getEntity(id.extend(DefaultKeys.PROPERTIES).extend("os_family"));
        Assertions.assertTrue(property.isPresent());
        Assertions.assertTrue(property.get() instanceof MappingEntity);
        Assertions.assertEquals("os_family", property.get().getName());
        Optional<Entity> type = model.getGraph().getEntity(id.extend(DefaultKeys.TYPE));
        Assertions.assertTrue(type.isPresent());
        Assertions.assertTrue(type.get() instanceof ScalarEntity);
        Assertions.assertEquals(EdmmTypeResolver.resolve(Compute.class), ((ScalarEntity) type.get()).getValue());
        model = new DeploymentModel(model.getName(), model.getGraph());
        Assertions.assertTrue(model.getComponent(id.getName()).isPresent());
        Assertions.assertEquals(1, model.getComponent(id.getName()).get().getProperties().size());
    }

    @Test
    void testAddComponentOfNewType() throws IllegalAccessException, IOException {
        EdmmTypeResolver.putMapping("test_type", TestComponentType.class);
        ClassPathResource resource = new ClassPathResource("edmm/four-components-hosted-on.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        EntityId id = Edmm.addComponent(model.getGraph(), "sauron", Map.of("wow", "very wow!"), TestComponentType.class);
        Assertions.assertEquals("sauron", id.getName());
        Optional<Entity> properties = model.getGraph().getEntity(id.extend(DefaultKeys.PROPERTIES));
        Assertions.assertTrue(properties.isPresent());
        Assertions.assertTrue(properties.get() instanceof MappingEntity);
        Optional<Entity> property = model.getGraph().getEntity(id.extend(DefaultKeys.PROPERTIES).extend("wow"));
        Assertions.assertTrue(property.isPresent());
        Assertions.assertTrue(property.get() instanceof MappingEntity);
        Assertions.assertEquals("wow", property.get().getName());
        Optional<Entity> type = model.getGraph().getEntity(id.extend(DefaultKeys.TYPE));
        Assertions.assertTrue(type.isPresent());
        Assertions.assertTrue(type.get() instanceof ScalarEntity);
        Assertions.assertEquals(EdmmTypeResolver.resolve(TestComponentType.class), ((ScalarEntity) type.get()).getValue());
        model = new DeploymentModel(model.getName(), model.getGraph());
        Assertions.assertTrue(model.getComponent(id.getName()).isPresent());
        Assertions.assertEquals(1, model.getComponent(id.getName()).get().getProperties().size());
        LOGGER.info(model.getComponent(id.getName()).get().getClass().getName());
        StringWriter writer = new StringWriter();
        model.getGraph().generateYamlOutput(writer);
        LOGGER.info(writer.toString());
        Assertions.assertTrue(model.getComponent(id.getName()).get() instanceof TestComponentType);
    }

    @Test
    void testBuildGraphFromScratch() throws IllegalAccessException {
        EntityGraph graph = new EntityGraph();
        final EntityId engineId = Edmm.addComponent(
                graph,
                "engine-1",
                Map.of(DockerEngine.DOCKER_ENGINE_URL.getName(), "http://docker.engine.com"),
                DockerEngine.class);
        final EntityId applicationContainerId = Edmm.addComponent(
                graph,
                "app1",
                Map.of(DockerContainer.CONTAINER_ID.getName(), "ABC",
                        DockerContainer.STATE.getName(), "Running",
                        DockerContainer.IMAGE_ID.getName(), "awsome.com/theImage"),
                DockerContainer.class
        );
        final EntityId vmId = Edmm.addComponent(
                graph,
                "ubuntu-1",
                Map.of(Compute.TYPE.getName(), "ubuntu",
                        Compute.OS_FAMILY.getName(), "linux",
                        Compute.PUBLIC_KEY.getName(), "ffff666ffff"),
                Compute.class
        );
        final EntityId dbmsId = Edmm.addComponent(
                graph,
                "mysqldbms",
                Map.of(MysqlDbms.PORT.getName(), "1234",
                        MysqlDbms.ROOT_PASSWORD.getName(), "iacmfisthebest@##"),
                MysqlDbms.class
        );
        final EntityId dbId = Edmm.addComponent(
                graph,
                "production-db",
                Map.of(MysqlDatabase.PASSWORD.getName(), "1234",
                        MysqlDatabase.USER.getName(), "falazigb",
                        MysqlDatabase.NAME.getName(), "user-data"),

                MysqlDatabase.class
        );

        Edmm.addRelation(graph, dbmsId, vmId, HostedOn.class);
        Edmm.addRelation(graph, dbId, dbmsId, HostedOn.class);
        Edmm.addRelation(graph, applicationContainerId, engineId, HostedOn.class);
        Edmm.addRelation(graph, applicationContainerId, dbId, ConnectsTo.class);
        DeploymentModel model = new DeploymentModel("myDeploymentModel", graph);
        StringWriter writer = new StringWriter();
        graph.generateYamlOutput(writer);
        LOGGER.info(writer.toString());

        Optional<RootComponent> component = model.getComponent(engineId.getName());
        Assertions.assertTrue(component.isPresent());
        Assertions.assertTrue(component.get() instanceof DockerEngine);
        Assertions.assertTrue(component.get().getProperties().size() > 0);
        Assertions.assertTrue(component.get().getProperties().containsKey(DockerEngine.DOCKER_ENGINE_URL.getName()));
        Assertions.assertEquals("http://docker.engine.com",
                component.get().getProperty(DockerEngine.DOCKER_ENGINE_URL.getName()).get().getValue());
    }

    @Test
    void testAddingPropertyProgrammatically() throws IllegalAccessException {
        EntityGraph graph = new EntityGraph();
        final EntityId engineId = Edmm.addComponent(
                graph,
                "engine_1",
                Map.of(DockerEngine.DOCKER_ENGINE_URL.getName(), "http://docker.engine.com"),
                DockerEngine.class);
        final EntityId stuff = Edmm.addComponent(
                graph,
                "stuff_1",
                new HashMap<>(),
                Paas.class);
        Edmm.addPropertyAssignments(graph, stuff, Map.of("aKey", "aValue", "bKey", "bValue"));
        DeploymentModel model = new DeploymentModel("myModel", graph);
        Map<String, Property> engineProperties = model.getComponent(engineId.getName()).orElseThrow().getProperties();
        Assertions.assertEquals(1, engineProperties.values().stream().filter(Property::isComputed).count());
        Assertions.assertTrue(engineProperties.containsKey(DockerEngine.DOCKER_ENGINE_URL.getName()));
        Assertions.assertEquals("http://docker.engine.com",
                engineProperties.get(DockerEngine.DOCKER_ENGINE_URL.getName()).getValue());
        Map<String, Property> stuffProperties = model.getComponent(stuff.getName()).orElseThrow().getProperties();

        Assertions.assertTrue(stuffProperties.containsKey("aKey"));
        Assertions.assertEquals("aValue",
                stuffProperties.get("aKey").getValue());
        Assertions.assertTrue(stuffProperties.containsKey("bKey"));
        Assertions.assertEquals("bValue",
                stuffProperties.get("bKey").getValue());
        Assertions.assertEquals(2, stuffProperties.values().stream().filter(Property::isComputed).count());
        StringWriter writer = new StringWriter();
        graph.generateYamlOutput(writer);
        LOGGER.info(writer.toString());
    }

    @Test
    void testFindTargetComponents() throws IOException {
        ClassPathResource resource = new ClassPathResource("edmm/four-components-hosted-on.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        RootComponent tomcat = model.getComponent("tomcat").orElseThrow();
        Collection<RootComponent> hosts = Edmm.findTargetComponents(model, tomcat, HostedOn.class);
        Assertions.assertNotNull(hosts);
        Assertions.assertEquals(1, hosts.size());
        Assertions.assertEquals("ubuntu", hosts.stream().findFirst().orElseThrow().getName());
    }

    @Test
    void testFindDbmsIp() throws IOException {
        final String IP = "172.17.0.1";
        ClassPathResource resource = new ClassPathResource("edmm/realworld_application_instance_model_docker_refined.yaml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        RootComponent db = model.getComponent("MySQL-DB_w1_0").orElseThrow();
        String ip = Edmm.findHostIp(db, model);
        Assertions.assertNotNull(ip);
        Assertions.assertEquals("localhost", ip);
        RootComponent dockerEngine = model.getComponent("DockerEngine").orElseThrow();
        ip = Edmm.findHostIp(dockerEngine, model);
        Assertions.assertNotNull(ip);
        Assertions.assertEquals(IP, ip);
    }
    
    @Test
    void testRemoveComponent() throws IllegalAccessException, IOException {
        ClassPathResource resource = new ClassPathResource("edmm/realworld_application_instance_model_docker_refined.yaml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        int relationsCount = model.getRelations().size();
        Collection<DockerContainer> containers = Edmm.getAllComponentsOfType(model, DockerContainer.class);
        Assertions.assertEquals(2, containers.size());

        Edmm.removeComponents(model.getGraph(), containers);
        model = new DeploymentModel(model.getName(), model.getGraph());
        containers = Edmm.getAllComponentsOfType(model, DockerContainer.class);
        Assertions.assertEquals(0, containers.size());
        Assertions.assertEquals(relationsCount - 5, model.getRelations().size());
    }
}
