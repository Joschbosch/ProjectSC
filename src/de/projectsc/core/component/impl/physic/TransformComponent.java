/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component.impl.physic;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.data.physics.Transform;

/**
 * This component manages the {@link transform} of an entity. It can not be removed.
 * 
 * @author Josch Bosch
 */
public class TransformComponent extends PhysicsComponent {

    /**
     * ID.
     */
    public static final String NAME = "Transform Component";

    private Transform transform = null;

    public TransformComponent() {
        setID(NAME);
        setType(ComponentType.PHYSICS);
    }

    @Override
    public void update() {
        if (owner != null) {
            transform = owner.getTransform();
        }
    }

    /**
     * Update to a new position determined by the given velocity.
     * 
     * @param entity to change the position
     * @param velocity to change
     * @param rotation of the entity
     */
    public void updatePosition(long entity, Vector3f velocity, Vector3f rotation) {
        Vector3f.add(transform.getPosition(), velocity, transform.getPosition());
        Vector3f.add(transform.getRotation(), rotation, transform.getRotation());
    }

    @Override
    public Map<String, Object> serialize(File savingLocation) {
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
    public void deserialize(Map<String, Object> serialized, File loadingLocation) {
        this.transform = new Transform();
        transform.setPosition(new Vector3f((float) (double) serialized.get("positionX"), (float) (double) serialized.get("positionY"),
            (float) (double) serialized.get("positionZ")));
        transform.setRotation(new Vector3f((float) (double) serialized.get("rotationX"), (float) (double) serialized.get("rotationY"),
            (float) (double) serialized.get("rotationZ")));
        transform.setScale(new Vector3f((float) (double) serialized.get("scaleX"), (float) (double) serialized.get("scaleY"),
            (float) (double) serialized.get("scaleZ")));
    }

    @Override
    public boolean isValidForSaving() {
        return true;
    }

    public Vector3f getPosition() {
        return transform.getPosition();
    }

    /**
     * @param position to set
     */
    public void setPosition(Vector3f position) {
        transform.getPosition().set(position);
    }

    public Vector3f getRotation() {
        return transform.getRotation();
    }

    /**
     * @param rotation to set
     */
    public void setRotation(Vector3f rotation) {
        transform.getRotation().set(rotation);
    }

    public Vector3f getScale() {
        return transform.getScale();
    }

    /**
     * @param scale to set
     */
    public void setScale(Vector3f scale) {
        transform.getScale().set(scale);
    }

}
