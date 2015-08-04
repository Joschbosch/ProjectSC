/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.editor;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.objects.GraphicalEntity;

public class EditorCamera extends Camera {

    private boolean rotateCamera;

    public EditorCamera(GraphicalEntity player) {
        super(player);
        distanceFromPlayer = 12;
        pitch = 40;
    }

    @Override
    public void move(float delta) {
        // super.move(delta);
        if (rotateCamera) {
            angleAroundPlayer += delta * 0.03f;
        }
        this.yaw = (0.0f - angleAroundPlayer);
        calculateCameraPosition(new Vector3f(0, 0, 0), distanceFromPlayer, angleAroundPlayer);
    }

    @Override
    protected void calculateCameraPosition(Vector3f lookAtPoint, float distanceFromPoint, float angle) {
        position.x = (float) (lookAtPoint.x + distanceFromPoint * Math.sin(Math.toRadians(angle)));
        position.z = (float) (lookAtPoint.z + distanceFromPoint * Math.cos(Math.toRadians(angle)));
    }

    public boolean isRotateCamera() {
        return rotateCamera;
    }

    public void setRotateCamera(boolean rotateCamera) {
        this.rotateCamera = rotateCamera;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }
}
