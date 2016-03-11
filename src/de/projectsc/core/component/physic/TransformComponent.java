/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component.physic;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.interfaces.Component;

/**
 * This component manages the {@link transform} of an entity. It can not be removed.
 * 
 * @author Josch Bosch
 */
public class TransformComponent extends DefaultComponent {

    /**
     * ID.
     */
    public static final String NAME = "Transform Component";

    private Transform transform = new Transform();
    
    public TransformComponent() {
        setComponentName(NAME);
        setType(ComponentType.PHYSICS);
    }

    /**
     * Update to a new position determined by the given velocity.
     * 
     * @param entity to change the position
     * @param velocity to change
     * @param angle of the entity
     */
    public void updatePosition(String entity, Vector3f velocity, float angle) {
        Vector3f.add(transform.getPosition(), velocity, transform.getPosition());
        transform.getRotation().y = angle;
    }

    @Override
    public Map<String, Object> getConfiguration() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("positionX", transform.getPosition().x);
        serialized.put("positionY", transform.getPosition().y);
        serialized.put("positionZ", transform.getPosition().z);
        serialized.put("rotationX", transform.getRotation().x);
        serialized.put("rotationY", transform.getRotation().y);
        serialized.put("rotationZ", transform.getRotation().z);
        serialized.put("scaleX", transform.getScale().x);
        serialized.put("scaleY", transform.getScale().y);
        serialized.put("scaleZ", transform.getScale().z);
        return serialized;
    }

    @Override
    public void loadConfiguration(Map<String, Object> serialized) {
        transform.setPosition(
            new Vector3f((float) (double) serialized.get("positionX"), (float) (double) serialized.get("positionY"),
                (float) (double) serialized.get("positionZ")));
        transform.setRotation(
            new Vector3f((float) (double) serialized.get("rotationX"), (float) (double) serialized.get("rotationY"),
                (float) (double) serialized.get("rotationZ")));
        transform.setScale(new Vector3f((float) (double) serialized.get("scaleX"), (float) (double) serialized.get("scaleY"),
            (float) (double) serialized.get("scaleZ")));
    }

    @Override
    public String serializeForNetwork() {
        return "" + transform.toString();
    }

    @Override
    public void deserializeFromNetwork(String serialized) {
        String[] split = serialized.split(";");
        transform.setPosition(new Vector3f(Float.valueOf(split[0]), Float.valueOf(split[1]), Float.valueOf(split[2])));
        transform.setRotation(new Vector3f(Float.valueOf(split[3]), Float.valueOf(split[4]), Float.valueOf(split[5])));
        transform.setScale(new Vector3f(Float.valueOf(split[6]), Float.valueOf(split[7]), Float.valueOf(split[8])));
    }

    @Override
    public Component cloneComponent() {
        TransformComponent tc = new TransformComponent();
        tc.setPosition(new Vector3f(getPosition()));
        tc.setRotation(new Vector3f(getRotation()));
        tc.setScale(new Vector3f(getScale()));
        return tc;
    }

    @Override
    public boolean isValidForEntitySaving() {
        return true;
    }

    public Vector3f getPosition() {
        return transform.getPosition();
    }

    /**
     * @param position to set
     */
    public void setPosition(Vector3f position) {
        if (owner != null) {
            transform.getPosition().set(position);
        }
    }

    public Vector3f getRotation() {
        return transform.getRotation();
    }

    /**
     * @param rotation to set
     */
    public void setRotation(Vector3f rotation) {
        if (owner != null) {
            transform.getRotation().set(rotation);
        }
    }

    public Vector3f getScale() {
        return transform.getScale();
    }

    /**
     * @param scale to set
     */
    public void setScale(Vector3f scale) {
        if (owner != null) {
            transform.getScale().set(scale);
        }
    }

    public Transform getTransform() {
        return transform;
    }

}
