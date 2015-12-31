/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component.physic;

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

    public TransformComponent() {
        setComponentName(NAME);
        setType(ComponentType.PHYSICS);
    }

    @Override
    public void update(long elapsed) {}

    /**
     * Update to a new position determined by the given velocity.
     * 
     * @param entity to change the position
     * @param velocity to change
     * @param rotation of the entity
     */
    public void updatePosition(String entity, Vector3f velocity, Vector3f rotation) {
        Vector3f.add(owner.getTransform().getPosition(), velocity, owner.getTransform().getPosition());
        Vector3f.add(owner.getTransform().getRotation(), rotation, owner.getTransform().getRotation());
    }

    @Override
    public Map<String, Object> serialize(File savingLocation) {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("positionX", owner.getTransform().getPosition().x);
        serialized.put("positionY", owner.getTransform().getPosition().y);
        serialized.put("positionZ", owner.getTransform().getPosition().z);
        serialized.put("rotationX", owner.getTransform().getRotation().x);
        serialized.put("rotationY", owner.getTransform().getRotation().y);
        serialized.put("rotationZ", owner.getTransform().getRotation().z);
        serialized.put("scaleX", owner.getTransform().getScale().x);
        serialized.put("scaleY", owner.getTransform().getScale().y);
        serialized.put("scaleZ", owner.getTransform().getScale().z);
        return serialized;
    }

    @Override
    public void deserialize(Map<String, Object> serialized, String loadingLocation) {
        owner.getTransform().setPosition(
            new Vector3f((float) (double) serialized.get("positionX"), (float) (double) serialized.get("positionY"),
                (float) (double) serialized.get("positionZ")));
        owner.getTransform().setRotation(
            new Vector3f((float) (double) serialized.get("rotationX"), (float) (double) serialized.get("rotationY"),
                (float) (double) serialized.get("rotationZ")));
        owner.getTransform().setScale(new Vector3f((float) (double) serialized.get("scaleX"), (float) (double) serialized.get("scaleY"),
            (float) (double) serialized.get("scaleZ")));
    }

    @Override
    public String serializeForNetwork() {
        return "" + owner.getTransform().toString();
    }

    @Override
    public void deserializeFromNetwork(String serialized) {
        String[] split = serialized.split(";");
        owner.getTransform().setPosition(new Vector3f(Float.valueOf(split[0]), Float.valueOf(split[1]), Float.valueOf(split[2])));
        owner.getTransform().setRotation(new Vector3f(Float.valueOf(split[3]), Float.valueOf(split[4]), Float.valueOf(split[5])));
        owner.getTransform().setScale(new Vector3f(Float.valueOf(split[6]), Float.valueOf(split[7]), Float.valueOf(split[8])));
    }

    @Override
    public boolean isValidForSaving() {
        return true;
    }

    public Vector3f getPosition() {
        return owner.getTransform().getPosition();
    }

    /**
     * @param position to set
     */
    public void setPosition(Vector3f position) {
        owner.getTransform().getPosition().set(position);
    }

    public Vector3f getRotation() {
        return owner.getTransform().getRotation();
    }

    /**
     * @param rotation to set
     */
    public void setRotation(Vector3f rotation) {
        owner.getTransform().getRotation().set(rotation);
    }

    public Vector3f getScale() {
        return owner.getTransform().getScale();
    }

    /**
     * @param scale to set
     */
    public void setScale(Vector3f scale) {
        owner.getTransform().getScale().set(scale);
    }

    public Transform getTransform() {
        return owner.getTransform();
    }

}
