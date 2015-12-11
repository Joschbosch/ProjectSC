/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.editor;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.modes.client.gui.objects.Camera;

/**
 * A camera object especially for the editor.
 * 
 * @author Josch Bosch
 */
public class EditorCamera extends Camera {

    private boolean rotateCamera;

    public EditorCamera() {
        distanceFromCenterPoint = 20;
        pitch = 40;
        setPosition(0f, 20f, 10f);
    }

    @Override
    public void move(float delta) {
        calculatePitch();
        calculateAngleAroundPlayer();
        if (rotateCamera) {
            angleAroundPlayer += delta * ANGLE_AROUND_PLAYER_FACTOR / 100f;
        }
        this.yaw = (0.0f - angleAroundPlayer);
        calculateCameraPosition(centeringPoint, distanceFromCenterPoint, angleAroundPlayer);
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

}
