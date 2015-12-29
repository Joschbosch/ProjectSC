/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.input;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.Event;

/**
 * The mouse was moved but not clicked.
 * 
 * @author Josch Bosch
 */
public class MousePositionChangedAction extends Event {

    /**
     * ID.
     */
    public static final String ID = MousePositionChangedAction.class.getName();

    private final Vector3f currentRay;

    private final Vector3f currentCameraPosition;

    public MousePositionChangedAction(Vector3f currentRay, Vector3f currentCameraPosition) {
        super(ID);
        this.currentRay = currentRay;
        this.currentCameraPosition = currentCameraPosition;
    }

    public Vector3f getCurrentRay() {
        return currentRay;
    }

    public Vector3f getCurrentCameraPosition() {
        return currentCameraPosition;
    }

}
