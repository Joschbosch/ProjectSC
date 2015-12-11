/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.entities;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.entities.components.physic.PhysicsComponent;
import de.projectsc.core.events.NewPositionEvent;

public class TransformComponent extends PhysicsComponent {

    public static final String NAME = "Transform Component";

    private Vector3f position = new Vector3f(0, 0, 0);

    private Vector3f rotation = new Vector3f(0, 0, 0);

    private Vector3f scale = new Vector3f(1, 1, 1);

    public TransformComponent() {
        setID(NAME);
        setType(ComponentType.PHYSICS);
    }

    @Override
    public void update(long ownerEntity) {

    }

    public void updatePosition(long entity, Vector3f velocity, Vector3f rotation) {
        Vector3f.add(position, velocity, position);
        Vector3f.add(this.rotation, rotation, this.rotation);
        fireEvent(new NewPositionEvent(entity, position, rotation));
    }

    @Override
    public Map<String, Object> serialize(File savingLocation) {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("positionX", position.x);
        serialized.put("positionY", position.y);
        serialized.put("positionZ", position.z);
        serialized.put("rotationX", rotation.x);
        serialized.put("rotationY", rotation.y);
        serialized.put("rotationZ", rotation.z);
        serialized.put("scaleX", scale.x);
        serialized.put("scaleY", scale.y);
        serialized.put("scaleZ", scale.z);
        return serialized;
    }

    @Override
    public void deserialize(Map<String, Object> serialized, File loadingLocation) {

    }

    @Override
    public boolean isValidForSaving() {
        return true;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

}
