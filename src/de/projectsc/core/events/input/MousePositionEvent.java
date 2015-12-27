/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.input;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.Event;

public class MousePositionEvent extends Event {

    public static final String ID = "MousePositionEvent";

    private final Vector3f currentRay;

    private final Vector3f currentCameraPosition;

    public MousePositionEvent(Vector3f currentRay, Vector3f currentCameraPosition) {
        super(ID, "");
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
