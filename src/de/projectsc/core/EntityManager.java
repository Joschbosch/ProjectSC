/*
 * Copyright (C) 2015
 */

package de.projectsc.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.data.entities.Component;
import de.projectsc.core.data.entities.Entity;

public class EntityManager {

    private static final Log LOGGER = LogFactory.getLog(EntityManager.class);

    private final Map<Long, Entity> entities = new HashMap<>();

    private final Map<Long, Map<String, Component>> componentsOfEntities = new HashMap<>();

    public long createNewEntity() {
        Entity e = new Entity();
        entities.put(e.getID(), e);
        LOGGER.debug("Created new entity " + e.getID());
        return e.getID();
    }

    public void addComponentToEntity(long id, String componentName) {
        Component c = ComponentRegistry.createComponent(componentName);
        if (c != null) {
            Map<String, Component> components = componentsOfEntities.get(id);
            if (components == null) {
                components = new HashMap<>();
            }
            components.put(componentName, c);
            componentsOfEntities.put(id, components);
            LOGGER.debug("Added component " + componentName + " to entity " + id);
        }
    }

    public void removeComponentFromEntity(long id, String componentName) {
        Map<String, Component> components = componentsOfEntities.get(id);
        if (components != null) {
            if (components.remove(componentName) != null) {
                LOGGER.debug("Removed component " + componentName + " from entity " + id);
            } else {
                LOGGER.debug("Could not remove component " + componentName + " from entity " + id);
            }
        }
    }

    public Component getComponent(long id, String componentName) {
        Map<String, Component> components = componentsOfEntities.get(id);
        if (components != null) {
            Component c = components.get(componentName);
            if (c != null) {
                return c;
            } else {
                LOGGER.debug("Component " + componentName + " not added to entity " + id);
            }
        } else {
            LOGGER.debug("No components added to entity " + id);
        }
        return null;

    }

    public void deleteEntity(long id) {
        if (entities.remove(id) != null) {
            LOGGER.debug("Removed entity " + id);
            componentsOfEntities.remove(id);
        }
    }
}
