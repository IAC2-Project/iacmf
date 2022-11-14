package org.iac2.common.utility;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.core.parser.ScalarEntity;
import io.github.edmm.core.parser.SequenceEntity;
import io.github.edmm.core.parser.support.DefaultKeys;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.DependsOn;
import io.github.edmm.model.relation.HostedOn;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.support.EdmmYamlBuilder;
import io.github.edmm.model.support.ModelEntity;
import io.github.edmm.model.support.TypeResolver;

public class Edmm {

    public static void addType(DeploymentModel deploymentModel, Class<? extends ModelEntity> componentType) throws IllegalAccessException {
        Set<Entity> existingTypes = deploymentModel.getGraph().getChildren(EntityGraph.COMPONENT_TYPES);

        // we couldn't find the type in the deployment model
        if (existingTypes.stream().noneMatch(e -> e.getName().equals(TypeResolver.resolve(componentType)))) {
            EntityGraph graph = deploymentModel.getGraph();
            final EntityId typeId = EntityGraph.COMPONENT_TYPES.extend(TypeResolver.resolve(componentType));
            graph.addEntity(new MappingEntity(typeId, graph));

            // only the attributes in child types of RootComponent and DependsOn are properties
            // also: if we are at the root component/relation type level, we have null as a parent type
            if (!componentType.equals(RootComponent.class) && !componentType.equals(DependsOn.class)) {
                // we are sure this is going to work, because we handle the case in which the current componentType is the
                // root type separately.
                Class<? extends ModelEntity> parentType = (Class<? extends ModelEntity>) componentType.getSuperclass();
                graph.addEntity(new ScalarEntity(TypeResolver.resolve(parentType), typeId.extend(DefaultKeys.EXTENDS), graph));
                Collection<Field> typeAttributesAsFields = Stream
                        .of(componentType.getDeclaredFields())
                        .filter(f -> f.getType() == Attribute.class)
                        .toList();

                if (typeAttributesAsFields.size() > 0) {
                    final EntityId propertiesId = typeId.extend(DefaultKeys.PROPERTIES);
                    graph.addEntity(new MappingEntity(propertiesId, graph));
                    Attribute<?> attribute;

                    for (Field typeAttributesAsField : typeAttributesAsFields) {
                        attribute = (Attribute<?>) typeAttributesAsField.get(null);
                        addPropertyDefinition(graph, attribute, propertiesId);
                    }
                }

                addType(deploymentModel, parentType);
            } else {
                graph.addEntity(new ScalarEntity("null", typeId.extend(DefaultKeys.EXTENDS), graph));
            }
        }
    }

    private static void addPropertyDefinition(EntityGraph graph, Attribute<?> attribute, EntityId propertiesId) {
        EntityId propertyId = propertiesId.extend(attribute.getName());
        String edmmType = switch (attribute.getType().getName()) {
            case "java.lang.Integer", "java.lang.Long" -> DefaultKeys.INTEGER;
            case "java.lang.Float", "java.lang.Double" -> DefaultKeys.FLOAT;
            default -> DefaultKeys.STRING;
        };

        graph.addEntity(new ScalarEntity(edmmType, propertyId, graph));
    }

    public static void addRelation(DeploymentModel deploymentModel,
                                   EntityId startComponentEntityId,
                                   EntityId targetComponentEntityId,
                                   Class<? extends RootRelation> relationType) throws IllegalAccessException {
        addType(deploymentModel, relationType);
        EntityId relationsEntityId = startComponentEntityId.extend(DefaultKeys.RELATIONS);
        EntityGraph graph = deploymentModel.getGraph();
        graph.addEntity(
                new SequenceEntity(relationsEntityId, graph));
        graph.addEntity(
                new ScalarEntity(targetComponentEntityId.getName(), relationsEntityId.extend(TypeResolver.resolve(relationType)), graph));
    }

    public static EntityId addComponent(DeploymentModel deploymentModel,
                                        String componentId,
                                        Map<String, String> attributeAssignments,
                                        Class<? extends RootComponent> componentType) throws IllegalAccessException {
        addType(deploymentModel, componentType);
        EntityGraph graph = deploymentModel.getGraph();
        EntityId componentEntityId = EntityGraph.COMPONENTS.extend(componentId);
        graph.addEntity(new MappingEntity(componentEntityId, graph));
        graph.addEntity(new ScalarEntity(TypeResolver.resolve(componentType), componentEntityId.extend(DefaultKeys.TYPE), graph));

        if (attributeAssignments.size() > 0) {
            // ideally, we should check whether these attributes are present in the component type declaration as properties
            EntityId propertiesId = componentEntityId.extend(DefaultKeys.PROPERTIES);
            graph.addEntity(new MappingEntity(propertiesId, graph));
            attributeAssignments.forEach((key, value) -> graph.addEntity(new ScalarEntity(value, propertiesId.extend(key), graph)));
        }

        return componentEntityId;
    }

    public static Collection<RootComponent> getDockerEngineComponents(DeploymentModel deploymentModel) {
        return deploymentModel.getComponents().stream().filter(c -> c.getProperties().containsKey("DockerEngineURL")).collect(Collectors.toList());
    }
}
