package org.iac2.service.utility;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.core.parser.ScalarEntity;
import io.github.edmm.core.parser.support.DefaultKeys;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.AwsBeanstalk;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.Platform;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.DependsOn;
import org.iac2.common.utility.Edmm;
import org.iac2.common.utility.EdmmTypeResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class EdmmTest {

    @Test
    void testAddType() throws IOException, IllegalAccessException {
        ClassPathResource resource = new ClassPathResource("edmm/four-components-hosted-on.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        Edmm.addType(model.getGraph(), AwsBeanstalk.class);
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
        Assertions.assertTrue(property.get() instanceof ScalarEntity);
        Assertions.assertEquals("flip-flop-os", ((ScalarEntity) property.get()).getValue());
        Optional<Entity> type = model.getGraph().getEntity(id.extend(DefaultKeys.TYPE));
        Assertions.assertTrue(type.isPresent());
        Assertions.assertTrue(type.get() instanceof ScalarEntity);
        Assertions.assertEquals(EdmmTypeResolver.resolve(Compute.class), ((ScalarEntity) type.get()).getValue());
        model = new DeploymentModel(model.getName(), model.getGraph());
        Assertions.assertTrue(model.getComponent(id.getName()).isPresent());
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
        Assertions.assertTrue(property.get() instanceof ScalarEntity);
        Assertions.assertEquals("very wow!", ((ScalarEntity) property.get()).getValue());
        Optional<Entity> type = model.getGraph().getEntity(id.extend(DefaultKeys.TYPE));
        Assertions.assertTrue(type.isPresent());
        Assertions.assertTrue(type.get() instanceof ScalarEntity);
        Assertions.assertEquals(EdmmTypeResolver.resolve(TestComponentType.class), ((ScalarEntity) type.get()).getValue());
        model = new DeploymentModel(model.getName(), model.getGraph());
        Assertions.assertTrue(model.getComponent(id.getName()).isPresent());
    }
}
