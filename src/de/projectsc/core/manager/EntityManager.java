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
import de.projectsc.core.entities.EntityImpl;
import de.projectsc.core.events.components.ComponentAddedEvent;
import de.projectsc.core.events.components.ComponentRemovedEvent;
import de.projectsc.core.events.entities.DeletedEntityEvent;
import de.projectsc.core.events.entities.NewEntityCreatedEvent;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.interfaces.Entity;

/**
 * This is the core class for using {@link Entity}(ies). All entities are created by this class and are stored here. Also, all components
 * that the entities have are in this class.
 * 
 * @author Josch Bosch
 */
public class EntityManager {

    private static final Log LOGGER = LogFactory.getLog(EntityManager.class);

    private final Map<Long, Entity> entities = new HashMap<>();

    private final Map<Long, Map<String, Component>> entityComponents = new HashMap<>();

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
    public long createNewEntity() {
        Entity e = new EntityImpl(this);
        entities.put(e.getID(), e);
        LOGGER.debug("Created new entity " + e.getID());
        addComponentToEntity(e.getID(), TransformComponent.NAME);
        eventManager.fireEvent(new NewEntityCreatedEvent(e.getID()));
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
    public Component addComponentToEntity(long id, String componentName) {
        Component c = componentManager.createComponent(componentName);
        c.setOwner(getEntity(id));
        addComponentToEntity(id, c);
        return c;
    }

    public Component addComponentToEntity(long id, Component c) {
        if (c != null) {
            Map<String, Component> components = entityComponents.get(id);
            if (components == null) {
                components = new HashMap<>();
            }
            components.put(c.getComponentName(), c);
            entityComponents.put(id, components);
            LOGGER.debug("Added component " + c.getComponentName() + " to entity " + id);
            eventManager.fireEvent(new ComponentAddedEvent(id, c));
            List<String> required = c.getRequiredComponents();
            for (String reqComponent : required) {
                if (!hasComponent(id, componentManager.getComponentClass(reqComponent))) {
                    Component reqCom = addComponentToEntity(id, reqComponent);
                    reqCom.addRequiredByComponent(c.getComponentName());
                    LOGGER.debug(String.format("Component %s added because it was required by %s", reqComponent, c.getComponentName()));
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
     * Removes the given component name from the given entity. This will only work, if there is no more component that requires the
     * component to delete.
     * 
     * @param id of the entity
     * @param componentName to remove
     */
    public void removeComponentFromEntity(long id, String componentName) {
        Map<String, Component> components = entityComponents.get(id);
        if (components != null) {
            Component toRemove = components.get(componentName);
            if (toRemove != null && toRemove.getRequiredBy().isEmpty()) {
                components.remove(componentName);
                for (String reqComponentName : toRemove.getRequiredComponents()) {
                    if (hasComponent(id, componentManager.getComponentClass(reqComponentName))) {
                        Component c = getComponent(id, reqComponentName);
                        c.removeRequiredByComponent(componentName);
                    }
                }
                LOGGER.debug("Removed component " + componentName + " from entity " + id);
                eventManager.fireEvent(new ComponentRemovedEvent(id, toRemove));
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
    public Component getComponent(long id, String componentName) {
        Map<String, Component> components = entityComponents.get(id);
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
    public Component getComponent(long entityId, Class<? extends Component> componentClass) {
        Map<String, Component> componentsOfEntity = entityComponents.get(entityId);
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
    public boolean hasComponent(Long entity, Class<? extends Component> clazz) {
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
    public Map<String, Component> getAllComponents(long id) {
        return entityComponents.get(id);
    }

    /**
     * Removes the given entity.
     * 
     * @param id of entity to remove
     */
    public void deleteEntity(long id) {
        if (entities.remove(id) != null) {
            LOGGER.debug("Removed entity " + id);
            entityComponents.remove(id);
            eventManager.fireEvent(new DeletedEntityEvent(id));
        }

    }

    /**
     * @param id of an entity
     * @return the entity with the given id
     */
    public Entity getEntity(long id) {
        return entities.get(id);
    }

    public Set<Long> getAllEntites() {
        return entities.keySet();
    }

}
