/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.core.entities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.components.Component;
import de.projectsc.core.components.ComponentType;

public class Entity {

    private static int idCount = 1000;

    protected Vector3f position;

    protected Vector3f rotation;

    protected float scale;

    protected Map<Class<? extends Component>, Component> components;

    protected Map<ComponentType, List<Component>> typeMap = new HashMap<>();

    private final long entityTypeID;

    private final long entityUID;

    public Entity(long entityTypeId) {
        super();
        entityTypeID = entityTypeId;
        entityUID = idCount++;
        components = new HashMap<>();
    }

    public void update(ComponentType type) {
        if (typeMap.get(type) != null) {
            for (Component c : typeMap.get(type)) {
                c.update(this);
            }
        }
    }

    public long getEntityTypeId() {
        return entityTypeID;
    }

    public long getID() {
        return entityUID;
    }

    public void addComponent(Component c) {
        components.put(c.getClass(), c);
        List<Component> type = typeMap.get(c.getType());
        if (type == null) {
            type = new LinkedList<>();
            typeMap.put(c.getType(), type);
        }
        type.add(c);
    }

    public void removeComponent(String component) {
        Entry<Class<? extends Component>, Component> toRemove = null;
        for (Entry<Class<? extends Component>, Component> c : components.entrySet()) {
            if (c.getValue().getComponentName().equals(component)) {
                toRemove = c;
                break;
            }
        }
        if (toRemove != null) {
            components.remove(toRemove.getKey());
            List<Component> type = typeMap.get(toRemove.getValue().getType());
            Component removetype = null;
            for (Component c : type) {
                if (c.getComponentName().equals(component)) {
                    removetype = c;
                }
            }
            if (removetype != null) {
                type.remove(removetype);
            }
        }
    }

    public <T> T getComponent(Class<T> clazz) {
        return clazz.cast(components.get(clazz));
    }

    public Map<Class<? extends Component>, Component> getComponents() {
        return components;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public float getRotX() {
        if (rotation != null) {
            return rotation.x;
        }
        return 0;
    }

    public float getRotY() {
        if (rotation != null) {
            return rotation.y;
        }
        return 0;
    }

    public float getRotZ() {
        if (rotation != null) {
            return rotation.z;
        }
        return 0;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setRotY(float targetRotation) {
        if (this.rotation != null) {
            this.rotation.y = targetRotation;
        }
    }

}
