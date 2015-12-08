/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events;

import de.projectsc.core.data.Event;

public class ChangeMovementParameterEvent extends Event {

    public static final String ID = "ChangeVelocityEvent";

    private float acceleration = Float.NEGATIVE_INFINITY;

    private float maximumSpeed = Float.NEGATIVE_INFINITY;

    private float turnSpeed = Float.NEGATIVE_INFINITY;

    private float currentSpeed = Float.NEGATIVE_INFINITY;

    public ChangeMovementParameterEvent(long entityId) {
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
