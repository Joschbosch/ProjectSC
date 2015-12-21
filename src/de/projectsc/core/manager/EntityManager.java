/*
 * Copyright (C) 2015
 */

package de.projectsc.core.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.component.impl.physic.TransformComponent;
import de.projectsc.core.entities.Entity;
import de.projectsc.core.events.components.ComponentAddedEvent;
import de.projectsc.core.events.components.ComponentRemovedEvent;
import de.projectsc.core.events.entities.NewEntityCreatedEvent;
import de.projectsc.core.interfaces.Component;

/**
 * This is the core class for using {@link Entity}(ies). All entities are created by this class and are stored here. Also, all components
 * that the entities have are in this class.
 * 
 * @author Josch Bosch
 */
public final class EntityManager {

    private static final Log LOGGER = LogFactory.getLog(EntityManager.class);

    private static final Map<Long, Entity> ENTITIES = new HashMap<>();

    private static final Map<Long, Map<String, Component>> ENTITYCOMPONENTS = new HashMap<>();

    private EntityManager() {

    }

    /**
     * Creates a new unique entity and fires an event that it has been created.
     * 
     * @return entity id
     */
    public static long createNewEntity() {
        Entity e = new Entity();
        ENTITIES.put(e.getID(), e);
        addComponentToEntity(e.getID(), TransformComponent.NAME);
        EventManager.fireEvent(new NewEntityCreatedEvent(e.getID()));
        LOGGER.debug("Created new entity " + e.getID());
        return e.getID();
    }

    /**
     * Adds a new component with the given name to the given entity. Also, all required components by the given component will be added if
     * not already there.
     * 
     * @param id of the entity to add the component
     * @param componentName of the component to add
     * @return the component that was created.
     */
    public static Component addComponentToEntity(long id, String componentName) {
        Component c = ComponentManager.createComponent(componentName);
        if (c != null) {
            Map<String, Component> components = ENTITYCOMPONENTS.get(id);
            if (components == null) {
                components = new HashMap<>();
            }
            components.put(componentName, c);
            ENTITYCOMPONENTS.put(id, components);
            LOGGER.debug("Added component " + componentName + " to entity " + id);
            List<String> required = c.getRequiredComponents();
            for (String reqComponent : required) {
                if (!hasComponent(id, ComponentManager.getComponentClass(reqComponent))) {
                    Component reqCom = addComponentToEntity(id, reqComponent);
                    reqCom.addRequiredByComponent(componentName);
                    LOGGER.debug(String.format("Component %s added because it was required by %s", reqComponent, componentName));
                } else {
                    Component reqCom = getComponent(id, reqComponent);
                    reqCom.addRequiredByComponent(componentName);
                }
            }
            EventManager.fireEvent(new ComponentAddedEvent(id, c));
            return c;
        }
        return null;
    }

    /**
     * Removes the given component name from the given entity. This will only work, if there is no more component that requires the
     * component to delete.
     * 
     * @param id of the entity
     * @param componentName to remove
     */
    public static void removeComponentFromEntity(long id, String componentName) {
        Map<String, Component> components = ENTITYCOMPONENTS.get(id);
        if (components != null) {
            Component toRemove = components.get(componentName);
            if (toRemove != null && toRemove.getRequiredBy().isEmpty()) {
                components.remove(componentName);
                for (String reqComponentName : toRemove.getRequiredComponents()) {
                    if (hasComponent(id, ComponentManager.getComponentClass(reqComponentName))) {
                        Component c = getComponent(id, reqComponentName);
                        c.removeRequiredByComponent(componentName);
                    }
                }
                LOGGER.debug("Removed component " + componentName + " from entity " + id);
                EventManager.fireEvent(new ComponentRemovedEvent(id, toRemove));
            } else {
                LOGGER.debug("Did not remove component " + componentName + " because it is required by following components: "
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
    public static Component getComponent(long id, String componentName) {
        Map<String, Component> components = ENTITYCOMPONENTS.get(id);
        if (components != null) {
            Component c = components.get(componentName);
            if (c != null) {
                return c;
            } else {
                LOGGER.debug("Component " + componentName + " not added to entity " + id);
            }
        } else {
            LOGGER.debug("No components were added to entity " + id);
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
    public static Component getComponent(long entityId, Class<? extends Component> componentClass) {
        Map<String, Component> componentsOfEntity = ENTITYCOMPONENTS.get(entityId);
        if (componentsOfEntity != null) {
            for (Component c : componentsOfEntity.values()) {
                if (componentClass.isInstance(c)) {
                    return c;
                }
            }
            LOGGER.debug("Component " + componentClass + " not added to entity " + entityId);
        } else {
            LOGGER.debug("No components were added to entity " + entityId);
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
    public static boolean hasComponent(Long entity, Class<? extends Component> clazz) {
        Map<String, Component> componentsOfEntity = ENTITYCOMPONENTS.get(entity);
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
    public static Map<String, Component> getAllComponents(long id) {
        return ENTITYCOMPONENTS.get(id);
    }

    /**
     * Removes the given entity.
     * 
     * @param id of entity to remove
     */
    public static void deleteEntity(long id) {
        if (ENTITIES.remove(id) != null) {
            LOGGER.debug("Removed entity " + id);
            ENTITYCOMPONENTS.remove(id);
        }
    }

    /**
     * @param id of an entity
     * @return the entity with the given id
     */
    public static Entity getEntity(long id) {
        return ENTITIES.get(id);
    }

    public static Set<Long> getAllEntites() {
        return ENTITIES.keySet();
    }

}
