/*
 * Copyright (C) 2015
 */

package de.projectsc.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.entities.Component;
import de.projectsc.core.entities.Entity;
import de.projectsc.core.events.NewEntityCreatedEvent;

public class EntityManager {

    private static final Log LOGGER = LogFactory.getLog(EntityManager.class);

    private static final Map<Long, Entity> ENTITIES = new HashMap<>();

    private static final Map<Long, Map<String, Component>> ENTITYCOMPONENTS = new HashMap<>();

    public static long createNewEntity() {
        Entity e = new Entity();
        ENTITIES.put(e.getID(), e);
        LOGGER.debug("Created new entity " + e.getID());
        EventManager.fireEvent(new NewEntityCreatedEvent(e.getID()));
        return e.getID();
    }

    public static Component addComponentToEntity(long id, String componentName) {
        Component c = ComponentRegistry.createComponent(componentName);
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
                if (!hasComponent(id, ComponentRegistry.getComponentClass(reqComponent))) {
                    Component reqCom = addComponentToEntity(id, reqComponent);
                    reqCom.addRequiredByComponent(componentName);
                    LOGGER.debug(String.format("Component %s added because it was required by %s", reqComponent, componentName));
                } else {
                    Component reqCom = getComponent(id, reqComponent);
                    reqCom.addRequiredByComponent(componentName);
                }
            }
            return c;
        }
        return null;
    }

    public static void removeComponentFromEntity(long id, String componentName) {
        Map<String, Component> components = ENTITYCOMPONENTS.get(id);
        if (components != null) {
            Component toRemove = components.get(componentName);
            if (toRemove != null && toRemove.getRequiredBy().isEmpty()) {
                components.remove(componentName);
                for (String reqComponentName : toRemove.getRequiredComponents()) {
                    if (hasComponent(id, ComponentRegistry.getComponentClass(reqComponentName))) {
                        Component c = getComponent(id, reqComponentName);
                        c.removeRequiredByComponent(componentName);
                    }
                }
                LOGGER.debug("Removed component " + componentName + " from entity " + id);
            } else {
                LOGGER.debug("Did not remove component " + componentName + " because it is required by following components: "
                    + toRemove.getRequiredBy());
            }
        }
    }

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

    public static Map<String, Component> getAllComponents(long id) {
        return ENTITYCOMPONENTS.get(id);
    }

    public static void deleteEntity(long id) {
        if (ENTITIES.remove(id) != null) {
            LOGGER.debug("Removed entity " + id);
            ENTITYCOMPONENTS.remove(id);
        }
    }

    public static Entity getEntity(long id) {
        return ENTITIES.get(id);
    }

    public static Set<Long> getAllEntites() {
        return ENTITIES.keySet();
    }

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

}
