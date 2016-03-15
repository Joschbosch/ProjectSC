/*
 * Copyright (C) 2015
 */

package de.projectsc.core.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.component.physic.TransformComponent;
import de.projectsc.core.component.state.EntityStateComponent;
import de.projectsc.core.entities.EntityImpl;
import de.projectsc.core.entities.EntitySchema;
import de.projectsc.core.events.entity.component.ComponentAddedEvent;
import de.projectsc.core.events.entity.component.ComponentRemovedEvent;
import de.projectsc.core.events.entity.objects.NotifyEntityCreatedEvent;
import de.projectsc.core.events.entity.objects.NotifyEntityDeletedEvent;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.interfaces.Entity;
import de.projectsc.core.utils.EntitySchemaLoader;

/**
 * This is the core class for using {@link Entity}(ies). All entities are created by this class and are stored here. Also, all components
 * that the entities have are in this class.
 * 
 * @author Josch Bosch
 */
public class EntityManager {

    private static final Log LOGGER = LogFactory.getLog(EntityManager.class);

    private final Map<String, Entity> entities = new HashMap<>();

    private final Map<Class<? extends Component>, Set<String>> componentToEntities = new HashMap<>();

    private final Map<String, Map<String, Component>> entityComponents = new HashMap<>();

    private final Map<Long, EntitySchema> entitySchemas = new HashMap<>();

    private ComponentManager componentManager;

    private EventManager eventManager;

    public EntityManager(ComponentManager componentManager, EventManager eventManager) {
        this.componentManager = componentManager;
        this.eventManager = eventManager;
    }

    /**
     * Creates a new unique entity and fires an event that it has been created.
     * 
     * @return entity id
     */
    public String createNewEntity() {
        String uuid = UUID.randomUUID().toString();
        String newEntity = createNewEntity(uuid);
        eventManager.fireEvent(new NotifyEntityCreatedEvent(newEntity));
        return newEntity;
    }

    /**
     * Create a new entity with the given UID.
     * 
     * @param uid to set
     * @return entity ID.
     */
    public String createNewEntity(String uid) {
        if (entities.get(uid) != null) {
            LOGGER.error("Entity with id " + uid + " already exists!");
            return "";
        }
        Entity e = new EntityImpl(this, uid);
        entities.put(e.getID(), e);
        LOGGER.info("Created new entity " + e.getID());
        addComponentToEntity(e.getID(), TransformComponent.NAME);
        addComponentToEntity(e.getID(), EntityStateComponent.NAME);
        return e.getID();
    }

    /**
     * Create a new entity using the given schema.
     * 
     * @param schemaId to load
     * @param entityUID to set
     * @return new entity
     */
    public String createNewEntityFromSchema(long schemaId, String entityUID) {
        String e = createNewEntity(entityUID);
        if (entitySchemas.get(schemaId) == null) {
            EntitySchema newSchema = EntitySchemaLoader.loadEntitySchema(schemaId, getEntity(e), componentManager);
            if (newSchema != null) {
                entitySchemas.put(newSchema.getId(), newSchema);
            } else {
                return "";
            }
        }
        EntitySchema schema = entitySchemas.get(schemaId);
        for (Component c : schema.getComponents()) {
            Component clone = c.cloneComponent();
            addComponentToEntity(e, clone);
            clone.setOwner(getEntity(e));
        }
        getEntity(e).setEntityTypeId(schema.getId());
        eventManager.fireEvent(new NotifyEntityCreatedEvent(e));
        return e;
    }

    /**
     * Create a new entity using the given schema.
     * 
     * @param schemaId to load
     * @return new entity
     */
    public String createNewEntityFromSchema(long schemaId) {
        return createNewEntityFromSchema(schemaId, UUID.randomUUID().toString());
    }

    /**
     * Adds a new component with the given name to the given entity. Also, all required components by the given component will be added if
     * not already there.
     * 
     * @param id of the entity to add the component
     * @param componentName of the component to add
     * @return the component that was created.
     */
    public Component addComponentToEntity(String id, String componentName) {
        Component c = componentManager.createComponent(componentName);
        c.setOwner(getEntity(id));
        addComponentToEntity(id, c);
        return c;
    }

    /**
     * Adds the specified component to the given entity.
     * 
     * @param id of entity
     * @param c component
     * @return the component
     */
    public Component addComponentToEntity(String id, Component c) {
        return addComponentToEntity(id, c, true);
    }

    /**
     * Adds the specified component to the given entity.
     * 
     * @param id of entity
     * @param c component
     * @param addToCollision true 
     * @return the component
     */
    public Component addComponentToEntity(String id, Component c, boolean addToCollision) {
        if (c != null) {
            Map<String, Component> components = entityComponents.get(id);
            if (components == null) {
                components = new HashMap<>();
            }
            components.put(c.getComponentName(), c);
            entityComponents.put(id, components);
            Set<String> entitiesWithcomponent = componentToEntities.get(c.getClass());
            if (entitiesWithcomponent == null) {
                entitiesWithcomponent = new HashSet<>();
                componentToEntities.put(c.getClass(), entitiesWithcomponent);
            }
            entitiesWithcomponent.add(id);
            LOGGER.info("Added component " + c.getComponentName() + " to entity " + id);
            eventManager.fireEvent(new ComponentAddedEvent(id, c));
            List<String> required = c.getRequiredComponents();
            for (String reqComponent : required) {
                if (!hasComponent(id, componentManager.getComponentClass(reqComponent))) {
                    Component reqCom = addComponentToEntity(id, reqComponent);
                    reqCom.addRequiredByComponent(c.getComponentName());
                    LOGGER.info(String.format("Component %s added because it was required by %s", reqComponent, c.getComponentName()));
                } else {
                    Component reqCom = getComponent(id, reqComponent);
                    reqCom.addRequiredByComponent(c.getComponentName());
                }
            }
            return c;
        }
        return null;
    }

    /**
     * Returns all entites that have the given component.
     * 
     * @param c component that an entity must have.
     * @return set of entities.
     */
    public Set<String> getEntitiesWithComponent(Class<? extends Component> c) {
        if (componentToEntities.get(c) == null) {
            return new HashSet<>();
        }
        return componentToEntities.get(c);
    }

    /**
     * Removes the given component name from the given entity. This will only work, if there is no more component that requires the
     * component to delete.
     * 
     * @param id of the entity
     * @param componentName to remove
     */
    public void removeComponentFromEntity(String id, String componentName) {
        Map<String, Component> components = entityComponents.get(id);
        if (components != null) {
            Component toRemove = components.get(componentName);
            if (toRemove != null && toRemove.getRequiredBy().isEmpty()) {
                components.remove(componentName);
                Set<String> entitesWithComponent = componentToEntities.get(toRemove.getClass());
                if (entitesWithComponent != null) {
                    entitesWithComponent.remove(id);
                }
                for (String reqComponentName : toRemove.getRequiredComponents()) {
                    if (hasComponent(id, componentManager.getComponentClass(reqComponentName))) {
                        Component c = getComponent(id, reqComponentName);
                        c.removeRequiredByComponent(componentName);
                    }
                }
                LOGGER.info("Removed component " + componentName + " from entity " + id);
                eventManager.fireEvent(new ComponentRemovedEvent(id, toRemove));
            } else {
                LOGGER.info("Did not remove component " + componentName + " because it is required by following components: "
                    + toRemove.getRequiredBy());
            }
        }
    }

    /**
     * Return the component with the given name from the given entity, if it has the component.
     * 
     * @param id of the entity
     * @param componentName to get
     * @return the component, if there
     */
    public Component getComponent(String id, String componentName) {
        Map<String, Component> components = entityComponents.get(id);
        if (components != null) {
            Component c = components.get(componentName);
            if (c != null) {
                return c;
            } else {
                LOGGER.info("Component " + componentName + " not added to entity " + id);
            }
        } else {
            LOGGER.info("No components were added to entity " + id);
        }
        return null;

    }

    /**
     * Return the component with the given name from the given entity, if it has the component.
     * 
     * @param entityId of the entity
     * @param componentClass to get
     * @return the component, if there
     */
    public Component getComponent(String entityId, Class<? extends Component> componentClass) {
        Map<String, Component> componentsOfEntity = entityComponents.get(entityId);
        if (componentsOfEntity != null) {
            for (Component c : componentsOfEntity.values()) {
                if (componentClass.isInstance(c)) {
                    return c;
                }
            }
            LOGGER.info("Component " + componentClass + " not added to entity " + entityId);
            try {
                throw new RuntimeException();
            } catch (RuntimeException e) {
                LOGGER.debug("Error getting component: ", e);
            }
        } else {
            LOGGER.info("No components were added to entity " + entityId);
        }
        return null;
    }

    /**
     * Checks if the entity with the id has the given component attached.
     * 
     * @param entity to check
     * @param clazz to check
     * @return true, if entity has component attached.
     */
    public boolean hasComponent(String entity, Class<? extends Component> clazz) {
        Map<String, Component> componentsOfEntity = entityComponents.get(entity);
        if (componentsOfEntity != null) {
            for (Component c : componentsOfEntity.values()) {
                if (clazz.isInstance(c)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return all components of an entity.
     * 
     * @param id of the entity
     * @return all components from id
     */
    public Map<String, Component> getAllComponents(String id) {
        return entityComponents.get(id);
    }

    /**
     * Removes the given entity.
     * 
     * @param id of entity to remove
     */
    public void deleteEntity(String id) {
        Entity removed = entities.remove(id);
        if (removed != null) {
            List<Component> remove = new LinkedList<>(getAllComponents(removed.getID()).values());
            for (Component c : remove) {
                removeComponentFromEntity(removed.getID(), c.getComponentName());
            }
            LOGGER.info("Removed entity " + id);
            eventManager.fireEvent(new NotifyEntityDeletedEvent(id));
        }
    }

    /**
     * @param id of an entity
     * @return the entity with the given id
     */
    public Entity getEntity(String id) {
        return entities.get(id);
    }

    public Set<String> getAllEntites() {
        return entities.keySet();
    }

    /**
     * Remove old entities and set new ones.
     * 
     * @param newEntities to set
     */
    public void addAllEntites(Map<String, Entity> newEntities) {
        entities.clear();
        entities.putAll(newEntities);
    }

    public Map<String, Entity> getEntites() {
        return entities;
    }

    public Map<String, Map<String, Component>> getEntityComponents() {
        return entityComponents;
    }
}
