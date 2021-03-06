/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.entity.movement;

import de.projectsc.core.data.EntityEvent;

/**
 * Changes the parameters used for movement.
 * 
 * @author Josch Bosch
 */
public class UpdateVelocityEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = UpdateVelocityEvent.class.getName();

    private float acceleration = Float.NEGATIVE_INFINITY;

    private float maximumSpeed = Float.NEGATIVE_INFINITY;

    private float turnSpeed = Float.NEGATIVE_INFINITY;

    private float currentSpeed = Float.NEGATIVE_INFINITY;

    public UpdateVelocityEvent(String entityId) {
        super(ID, entityId);
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

    public void setCurrentSpeed(float currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public static String getId() {
        return ID;
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
