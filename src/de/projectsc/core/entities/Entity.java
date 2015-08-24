/*
 * Project SC - 2015
 * 
 * 
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
import de.projectsc.core.components.impl.BoundingComponent;
import de.projectsc.core.components.impl.MovingComponent;
import de.projectsc.core.utils.BoundingBox;
import de.projectsc.core.utils.PhysicalObject;

/**
 * All game objects in the game are entities. All entities have components, that define their role
 * and behavior.
 * 
 * @author Josch Bosch
 */
public class Entity implements PhysicalObject {

    private static int idCount = 1000;

    protected Vector3f position;

    protected Vector3f rotation;

    protected float scale;

    protected boolean selected = false;

    protected Map<Class<? extends Component>, Component> components;

    protected Map<ComponentType, List<Component>> typeMap = new HashMap<>();

    private final long entityTypeID;

    private final long entityUID;

    private boolean highlighted;

    public Entity(long entityTypeId) {
        super();
        entityTypeID = entityTypeId;
        entityUID = idCount++;
        components = new HashMap<>();
    }

    /**
     * Update all components with the given type.
     * 
     * @param type to update
     */
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

    /**
     * Add component to entity.
     * 
     * @param c component to add.
     */
    public void addComponent(Component c) {
        components.put(c.getClass(), c);
        List<Component> type = typeMap.get(c.getType());
        if (type == null) {
            type = new LinkedList<>();
            typeMap.put(c.getType(), type);
        }
        type.add(c);
    }

    /**
     * Remove component from entity.
     * 
     * @param component to remove
     */
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

    /**
     * Get component from entity, null if there is no component of this class.
     * 
     * @param <T> class
     * @param clazz of component to get.
     * @return component or null
     */
    public <T> T getComponent(Class<T> clazz) {
        return clazz.cast(components.get(clazz));
    }

    /**
     * 
     * Get component from entity, null if there is no component of this name.
     * 
     * @param name of component
     * @return component or null
     */
    public Component getComponentByName(String name) {
        for (Entry<Class<? extends Component>, Component> c : components.entrySet()) {
            if (c.getValue().getComponentName().equals(name)) {
                return (c.getKey().cast(c.getValue()));
            }
        }
        return null;
    }

    /**
     * Checks if the entity has the given component.
     * 
     * @param <T> clazz type
     * @param clazz to look up
     * @return true if component is there.
     */
    public <T> boolean hasComponent(Class<T> clazz) {
        return components.containsKey(clazz);
    }

    /**
     * Checks if the entity has the given component.
     * 
     * @param name to look up
     * @return true if component is there.
     */
    public boolean hasComponent(String name) {
        for (Entry<Class<? extends Component>, Component> c : components.entrySet()) {
            if (c.getValue().getComponentName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public Map<Class<? extends Component>, Component> getComponents() {
        return components;
    }

    @Override
    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    /**
     * Get x rotation of entity.
     * 
     * @return rotation
     */
    public float getRotX() {
        if (rotation != null) {
            return rotation.x;
        }
        return 0;
    }

    /**
     * Get y rotation of entity.
     * 
     * @return rotation
     */
    public float getRotY() {
        if (rotation != null) {
            return rotation.y;
        }
        return 0;
    }

    /**
     * Get z rotation of entity.
     * 
     * @return rotation
     */
    public float getRotZ() {
        if (rotation != null) {
            return rotation.z;
        }
        return 0;
    }

    /**
     * Sets the position.
     * 
     * @param position to set
     */
    public void setPosition(Vector3f position) {
        if (this.position == null) {
            this.position = position;
        } else {
            this.position.x = position.x;
            this.position.y = position.y;
            this.position.z = position.z;
        }
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * Set y rotation of entity.
     * 
     * @param targetRotation to set
     */
    public void setRotY(float targetRotation) {
        if (this.rotation != null) {
            this.rotation.y = targetRotation;
        }
    }

    @Override
    public boolean isMovable() {
        return hasComponent(MovingComponent.class);
    }

    @Override
    public BoundingBox getBoundingBox() {
        if (hasComponent(BoundingComponent.class)) {
            return getComponent(BoundingComponent.class).getBox();
        }
        return null;
    }

    @Override
    public boolean hasMoved() {
        if (hasComponent(MovingComponent.class)) {
            return getComponent(MovingComponent.class).isMoved();

        }
        return false;
    }

    public void setSelected(boolean b) {
        selected = b;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

}
