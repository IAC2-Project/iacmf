package org.iac2.common.utility;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.core.parser.ScalarEntity;
import io.github.edmm.core.parser.SequenceEntity;
import io.github.edmm.core.parser.support.DefaultKeys;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.relation.DependsOn;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.support.Attribute;
import io.github.edmm.model.support.ModelEntity;

public class Edmm {

    public static Entity addType(EntityGraph graph, Class<? extends ModelEntity> componentType) throws IllegalAccessException {
        Set<Entity> existingTypes = graph.getChildren(EntityGraph.COMPONENT_TYPES);
        final EntityId typeId = EntityGraph.COMPONENT_TYPES.extend(EdmmTypeResolver.resolve(componentType));
        final MappingEntity typeEntity = new MappingEntity(typeId, graph);

        // we couldn't find the type in the deployment model
        if (!existingTypes.contains(typeEntity)) {
            graph.addEntity(typeEntity);

            // only the attributes in child types of RootComponent and DependsOn are properties
            // also: if we are at the root component/relation type level, we have null as a parent type
            if (!componentType.equals(RootComponent.class) && !componentType.equals(DependsOn.class)) {
                // we are sure this is going to work, because we handle the case in which the current componentType is the
                // root type separately.
                Class<? extends ModelEntity> parentType = (Class<? extends ModelEntity>) componentType.getSuperclass();
                addType(graph, parentType);
                final EntityId parentTypeId = EntityGraph.COMPONENT_TYPES.extend(EdmmTypeResolver.resolve(parentType));
                final Entity parentEntity = graph.getEntity(parentTypeId).orElseThrow();
                graph.addEntity(new ScalarEntity(EdmmTypeResolver.resolve(parentType), typeId.extend(DefaultKeys.EXTENDS), graph));
                graph.addEdge(typeEntity, parentEntity, DefaultKeys.EXTENDS_TYPE);
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
            } else {
                graph.addEntity(new ScalarEntity("null", typeId.extend(DefaultKeys.EXTENDS), graph));
            }
        }

        return typeEntity;
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

    public static void addRelation(EntityGraph graph,
                                   EntityId startComponentEntityId,
                                   EntityId targetComponentEntityId,
                                   Class<? extends RootRelation> relationType) throws IllegalAccessException {
        addType(graph, relationType);
        EntityId relationsEntityId = startComponentEntityId.extend(DefaultKeys.RELATIONS);
        Set<Entity> currentRelations = graph.getChildren(relationsEntityId);

        if (currentRelations.size() == 0) {
            graph.addEntity(
                    new SequenceEntity(relationsEntityId, graph));
        }

        // we create an index for the relation
        EntityId indexId = relationsEntityId.extend(String.valueOf(currentRelations.size()));
        graph.addEntity(new MappingEntity(indexId, graph));

        final String relationTypeString = EdmmTypeResolver.resolve(relationType);
        MappingEntity normalizedEntity = new MappingEntity(indexId.extend(relationTypeString), graph);
        ScalarEntity type = new ScalarEntity(relationTypeString, normalizedEntity.getId().extend(DefaultKeys.TYPE), graph);
        ScalarEntity target = new ScalarEntity(targetComponentEntityId.getName(), normalizedEntity.getId().extend(DefaultKeys.TARGET), graph);

        graph.addEntity(normalizedEntity);
        graph.addEntity(type);
        graph.addEntity(target);
    }

    public static EntityId addComponent(EntityGraph graph,
                                        String componentId,
                                        Map<String, String> attributeAssignments,
                                        Class<? extends RootComponent> componentType) throws IllegalAccessException {
        final Entity typeEntity = addType(graph, componentType);
        EntityId componentEntityId = EntityGraph.COMPONENTS.extend(componentId);
        MappingEntity componentEntity = new MappingEntity(componentEntityId, graph);
        graph.addEntity(componentEntity);
        graph.addEntity(new ScalarEntity(EdmmTypeResolver.resolve(componentType), componentEntityId.extend(DefaultKeys.TYPE), graph));
        graph.addEdge(componentEntity, typeEntity, DefaultKeys.INSTANCE_OF);

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
