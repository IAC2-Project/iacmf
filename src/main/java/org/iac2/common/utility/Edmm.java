package org.iac2.common.utility;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
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
        if (graph.getEntity(EntityGraph.COMPONENT_TYPES).isEmpty()) {
            graph.addEntity(new MappingEntity(EntityGraph.COMPONENT_TYPES, graph));
        }

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
                    final Entity propertiesEntity = new MappingEntity(propertiesId, graph);
                    graph.addEntity(propertiesEntity);
                    Attribute<?> attribute;

                    for (Field typeAttributesAsField : typeAttributesAsFields) {
                        attribute = (Attribute<?>) typeAttributesAsField.get(null);
                        addPropertyDefinition(attribute, propertiesEntity);
                    }
                }
            } else {
                graph.addEntity(new ScalarEntity(null, typeId.extend(DefaultKeys.EXTENDS), graph));
            }
        }

        return typeEntity;
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
                                        Map<String, Object> attributeAssignments,
                                        Class<? extends RootComponent> componentType) throws IllegalAccessException {
        final Entity typeEntity = addType(graph, componentType);
        EntityId componentEntityId = EntityGraph.COMPONENTS.extend(componentId);

        if (graph.getEntity(EntityGraph.COMPONENTS).isEmpty()) {
            graph.addEntity(new MappingEntity(EntityGraph.COMPONENTS, graph));
        }

        MappingEntity componentEntity = new MappingEntity(componentEntityId, graph);
        graph.addEntity(componentEntity);

        graph.addEntity(new ScalarEntity(EdmmTypeResolver.resolve(componentType), componentEntityId.extend(DefaultKeys.TYPE), graph));
        graph.addEdge(componentEntity, typeEntity, DefaultKeys.INSTANCE_OF);

        if (attributeAssignments.size() > 0) {
            // ideally, we should check whether these attributes are present in the component type declaration as properties
            EntityId propertiesId = componentEntityId.extend(DefaultKeys.PROPERTIES);
            MappingEntity propertiesEntity = new MappingEntity(propertiesId, graph);
            graph.addEntity(propertiesEntity);
            addPropertyAssignments(propertiesEntity, attributeAssignments);
        }

        return componentEntityId;
    }

    public static void addPropertyAssignments(EntityGraph graph, EntityId componentId, Map<String, Object> attributeAssignments) {
        final Entity propertiesEntity = ensurePropertiesEntityExists(graph, componentId);

        addPropertyAssignments(propertiesEntity, attributeAssignments);
    }

    public static void addPropertyExpressionAssignment(EntityGraph graph, EntityId componentId, String name, String type, String expression) {
        final Entity propertiesEntity = ensurePropertiesEntityExists(graph, componentId);
        addProperty(propertiesEntity, type, name, true, expression);
    }

    public static String getComponentType(EntityGraph graph, EntityId componentId) {
        final EntityId componentTypeId = componentId.extend(DefaultKeys.TYPE);
        return ((ScalarEntity) graph.getEntity(componentTypeId).orElseThrow()).getValue();
    }

    public static <T extends RootComponent> Collection<T> getAllComponentsOfType(DeploymentModel deploymentModel, Class<T> type) {
        return deploymentModel.getComponents()
                .stream()
                .filter(c -> c.getClass().equals(type))
                .map(c -> (T) c)
                .toList();
    }

    public static Collection<RootComponent> findDependentComponents(DeploymentModel model,
                                                                    RootComponent targetComponent,
                                                                    Class<? extends DependsOn> relationType) {
        Collection<EntityId> hostedOnEngineRelIds = model.getRelations()
                .stream()
                .filter(r -> relationType.isAssignableFrom(r.getClass()) && r.getTarget().equals(targetComponent.getId()))
                .map(r -> r.getEntity().getId())
                .toList();
        // next, we find the sources of these relations
        return model.getComponents()
                .stream()
                .filter(c -> c.getRelations().stream().anyMatch(r -> hostedOnEngineRelIds.contains(r.getEntity().getId())))
                .toList();
    }

    private static void addPropertyDefinition(Attribute<?> attribute, Entity propertiesEntity) {
        String propertyName = attribute.getName();
        String edmmType = switch (attribute.getType().getName()) {
            case "java.lang.Integer", "java.lang.Long" -> DefaultKeys.INTEGER;
            case "java.lang.Float", "java.lang.Double" -> DefaultKeys.FLOAT;
            default -> DefaultKeys.STRING;
        };

        addProperty(propertiesEntity, edmmType, propertyName, false, null);
    }

    private static void addPropertyAssignments(Entity propertiesEntity, Map<String, Object> attributeAssignments) {

        attributeAssignments.forEach((key, value) -> {
            String type = EdmmTypeResolver.resolveBasicType(value.getClass());
            addProperty(propertiesEntity, type, key, true, convertAttributeValue(type, value));
        });
    }

    private static String convertAttributeValue(String edmmBasicType, Object value) {
        if (edmmBasicType.equals("list")) {
            return String.join(",", (Collection<String>)value);
        }

        return String.valueOf(value);
    }

    private static Entity ensurePropertiesEntityExists(EntityGraph graph, EntityId componentId) {
        final EntityId propertiesId = componentId.extend(DefaultKeys.PROPERTIES);
        return graph.getEntity(propertiesId).orElseGet(() -> {
            MappingEntity properties = new MappingEntity(propertiesId, graph);
            graph.addEntity(properties);
            return properties;
        });
    }

    /***
     * Adds a property to a type or a property assignment to a component. In the latter case, if the component already has
     * the property, its value is replaced with the new value. If the property being assigned to does not exist in the type
     * definition, it is also added there.
     * @param propertiesEntity the entity that holds the list of properties within the current type/component entity.
     * @param propertyType the type of the property
     * @param propertyName the name of the property
     * @param isAssignment indicates whether this is an assignment or a property definition
     * @param value if assignment, holds the value to be assigned.
     */
    private static void addProperty(Entity propertiesEntity, String propertyType, String propertyName, boolean isAssignment, String value) {
        final EntityGraph graph = propertiesEntity.getGraph();
        final EntityId propertiesId = propertiesEntity.getId();
        EntityId propertyId = propertiesId.extend(propertyName);
        MappingEntity normalizedEntity;
        boolean propertyExists = graph.getEntity(propertyId).isPresent();

        if (!propertyExists) {
            normalizedEntity = new MappingEntity(propertyId, graph);
            ScalarEntity typeEntity = new ScalarEntity(propertyType, normalizedEntity.getId().extend(DefaultKeys.TYPE), graph);
            graph.addEntity(normalizedEntity);
            graph.addEntity(typeEntity);
        } else {
            normalizedEntity = (MappingEntity) graph.getEntity(propertyId).get();
        }

        if (isAssignment) {
            ScalarEntity valueEntity = new ScalarEntity(value, normalizedEntity.getId().extend(DefaultKeys.VALUE), graph);
            if (propertyExists) {
                graph.replaceEntity(graph.getEntity(valueEntity.getId()).orElseThrow(), valueEntity);
            } else {
                graph.addEntity(valueEntity);
                ScalarEntity computedEntity = new ScalarEntity("true", normalizedEntity.getId().extend(DefaultKeys.COMPUTED), graph);
                graph.addEntity(computedEntity);
            }

            final EntityId componentId = propertiesEntity.getParent().orElseThrow().getId();

            if (!doesPropertyDefinitionExist(graph, componentId, propertyName)) {
                final String componentType = getComponentType(graph, componentId);
                final Entity propertyDefinitionsEntity = ensurePropertiesEntityExists(graph, EntityGraph
                        .COMPONENT_TYPES
                        .extend(componentType)
                        .extend(DefaultKeys.PROPERTIES));
                addProperty(propertyDefinitionsEntity, propertyType, propertyName, false, null);
            }
        }
    }

    private static boolean doesPropertyDefinitionExist(EntityGraph graph, EntityId componentId, String propertyName) {
        final String componentType = getComponentType(graph, componentId);
        final EntityId propertyDefinitionId = EntityGraph
                .COMPONENT_TYPES
                .extend(componentType)
                .extend(DefaultKeys.PROPERTIES)
                .extend(propertyName);

        return graph.getEntity(propertyDefinitionId).isPresent();
    }
}
