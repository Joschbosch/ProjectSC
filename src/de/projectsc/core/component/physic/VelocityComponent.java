/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component.physic;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.CoreConstants;
import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.state.EntityStateComponent;
import de.projectsc.core.interfaces.Component;

/**
 * Component that handles the movement of an entity.
 * 
 * @author Josch Bosch
 */
public class VelocityComponent extends PhysicsComponent {

    /**
     * ID.
     */
    public static final String NAME = "Velocity Component";

    private float acceleration = 10000f; // instant maximum speed

    private float maximumSpeed = 16f;

    private float maximumTurnSpeed = 1200f;

    private float turnSpeed = maximumTurnSpeed;

    private float currentSpeed = 0;

    private Vector3f velocity = new Vector3f(0, 0, 0);

    private Vector3f rotationDelta = new Vector3f(0, 0, 0);

    public VelocityComponent() {
        setComponentName(NAME);
        setType(ComponentType.PREPHYSICS);
        this.requiredComponents.add(EntityStateComponent.NAME);
        this.requiredComponents.add(PathComponent.NAME);

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
    public void deserialize(Map<String, Object> serialized, String loadingLocation) {
        acceleration = (float) (double) serialized.get("acceleration");
        maximumSpeed = (float) (double) serialized.get("maxSpeed");
        turnSpeed = (float) (double) serialized.get("turnSpeed");
    }

    @Override
    public String serializeForNetwork() {
        return "" + acceleration + CoreConstants.SERIALIZATION_SEPARATOR + maximumSpeed + CoreConstants.SERIALIZATION_SEPARATOR
            + maximumTurnSpeed + CoreConstants.SERIALIZATION_SEPARATOR + turnSpeed + CoreConstants.SERIALIZATION_SEPARATOR
            + currentSpeed + CoreConstants.SERIALIZATION_SEPARATOR + velocity.x
            + CoreConstants.SERIALIZATION_SEPARATOR + velocity.z + CoreConstants.SERIALIZATION_SEPARATOR + rotationDelta.y;
    }

    @Override
    public void deserializeFromNetwork(String serialized) {
        String[] split = serialized.split(";");
        acceleration = Float.valueOf(split[0]);
        maximumSpeed = Float.valueOf(split[1]);
        maximumTurnSpeed = Float.valueOf(split[2]);
        turnSpeed = Float.valueOf(split[3]);
        currentSpeed = Float.valueOf(split[4]);
        velocity.x = Float.valueOf(split[5]);
        velocity.y = Float.valueOf(split[6]);
        velocity.z = Float.valueOf(split[7]);
    }

    @Override
    public Component cloneComponent() {
        VelocityComponent vc = new VelocityComponent();
        vc.setAcceleration(acceleration);
        vc.setMaximumSpeed(maximumSpeed);
        vc.setMaximumTurnSpeed(maximumTurnSpeed);
        vc.setTurnSpeed(turnSpeed);
        vc.setCurrentSpeed(currentSpeed);
        vc.setVelocity(new Vector3f(velocity));
        vc.setRotationDelta(new Vector3f(rotationDelta));
        return vc;
    }

    @Override
    public boolean isValidForEntitySaving() {
        return true;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public Vector3f getRotationDelta() {
        return rotationDelta;
    }

    public void setCurrentSpeed(float f) {
        this.currentSpeed = maximumSpeed;
    }

    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public void setMaximumSpeed(float maximumSpeed) {
        this.maximumSpeed = maximumSpeed;
    }

    public void setTurnSpeed(float turnSpeed) {
        this.turnSpeed = maximumTurnSpeed;
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

    public float getMaximumTurnSpeed() {
        return maximumTurnSpeed;
    }

    public void setRotationDelta(Vector3f newRotationSpeed) {
        this.rotationDelta = newRotationSpeed;
    }

    public void setMaximumTurnSpeed(float maximumTurnSpeed) {
        this.maximumTurnSpeed = maximumTurnSpeed;
    }
}
