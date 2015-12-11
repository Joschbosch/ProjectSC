/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component.impl.physic;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.impl.behaviour.EntityStateComponent;
import de.projectsc.core.data.Timer;

public class VelocityComponent extends PhysicsComponent {

    public static final String NAME = "Velocity Component";

    private float acceleration = 10000f; // instant maximum speed

    private float maximumSpeed = 16f;

    private float turnSpeed = 60;

    private float currentSpeed = 0;

    private Vector3f velocity = new Vector3f(0, 0, 0);

    public VelocityComponent() {
        setID(NAME);
        setType(ComponentType.PREPHYSICS);
        this.requiredComponents.add(EntityStateComponent.NAME);
    }

    @Override
    public void update(long ownerEntity) {

    }

    public void updateVelocity(Vector3f rotation) {
        currentSpeed += acceleration * Timer.getDelta();
        if (currentSpeed >= maximumSpeed) {
            currentSpeed = maximumSpeed;
        }
        float distance = currentSpeed * Timer.getDelta() / 1000.0f;
        float dx = (float) (distance * Math.sin(Math.toRadians(rotation.y)));
        float dz = (float) (distance * Math.cos(Math.toRadians(rotation.y)));
        setVelocity(new Vector3f(dx, 0, dz));
    }

    @Override
    public Map<String, Object> serialize(File savingLocation) {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("acceleration", acceleration);
        serialized.put("maxSpeed", maximumSpeed);
        serialized.put("turnSpeed", turnSpeed);
        return serialized;
    }

    @Override
    public void deserialize(Map<String, Object> serialized, File loadingLocation) {
        acceleration = (float) (double) serialized.get("acceleration");
        maximumSpeed = (float) (double) serialized.get("maxSpeed");
        turnSpeed = (float) (double) serialized.get("turnSpeed");
    }

    @Override
    public boolean isValidForSaving() {
        return true;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public Vector3f getRotationDelta() {
        return new Vector3f(0, 0, 0);
    }

    public void setCurrentSpeed(float f) {
        this.currentSpeed = f;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public void setMaximumSpeed(float maximumSpeed) {
        this.maximumSpeed = maximumSpeed;
    }

    public void setTurnSpeed(float turnSpeed) {
        this.turnSpeed = turnSpeed;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public float getMaximumSpeed() {
        return maximumSpeed;
    }

    public float getTurnSpeed() {
        return turnSpeed;
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

}
