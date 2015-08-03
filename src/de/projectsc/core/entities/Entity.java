/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.entities;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.components.Component;

public class Entity {

    private static int idCount = 1000;

    private final long ENTITY_TYPE_ID;

    private final long ENTITY_UNIQUE_ID;

    protected Vector3f position;

    protected Vector3f rotation;

    protected float scale;

    protected Map<Class<? extends Component>, Component> components;

    public Entity(long entityTypeId) {
        super();
        ENTITY_TYPE_ID = entityTypeId;
        ENTITY_UNIQUE_ID = idCount++;
        components = new HashMap<>();
    }

    public long getEntityTypeId() {
        return ENTITY_TYPE_ID;
    }

    public long getID() {
        return ENTITY_UNIQUE_ID;
    }

    public void addComponent(Component c) {
        components.put(c.getClass(), c);
    }

    public <T> T getComponent(Class<T> clazz) {
        return clazz.cast(components.get(clazz));
    }

    public Map<Class<? extends Component>, Component> getComponents() {
        return components;
    }
}
