/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.input;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.Event;

public class MouseButtonClickedEvent extends Event {

    public static final String ID = "MousePositionEvent";

    private final Vector3f currentRay;

    private final Vector3f currentCameraPosition;

    private final int button;

    private final boolean repeated;

    public MouseButtonClickedEvent(int button, boolean repeated, Vector3f currentRay, Vector3f currentCameraPosition) {
        super(ID, "");
        this.button = button;
        this.repeated = repeated;
        this.currentRay = currentRay;
        this.currentCameraPosition = currentCameraPosition;
    }

    public Vector3f getCurrentRay() {
        return currentRay;
    }

    public Vector3f getCurrentCameraPosition() {
        return currentCameraPosition;
    }

    public int getButton() {
        return button;
    }

    public boolean isRepeated() {
        return repeated;
    }

}
