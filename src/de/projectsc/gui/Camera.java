/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * The Camera class provides all matrices necessary for calculating the current frame. Input control
 * for changing the carma is also done here.
 *
 * @author Josch Bosch
 */
public class Camera {

    private static final float TIME_SCALING = 1000.0f;

    private static final float DEGREES_360 = 360.0f;

    private static final float LOWERING_SPEED_SLOW = 5.0f;

    private static final float LOWERING_SPEED_FAST = 0.5f;

    private static final float MOVEMENT_SPEED_SLOW = 5f; // UPS

    private static final float MOVEMENT_SPEED_FAST = 0.5f; // UPS

    private static final float ROTATION_SPEED_SLOW = 11.25f; // UPS

    private static final float ROTATION_SPEED_FAST = 1.125f; // UPS

    private final Vector3f cameraPos;

    private final Vector3f sphereCamRelPos = new Vector3f(67.5f, -46.0f, 150.0f);

    private Vector3f camTarget = new Vector3f(0.0f, 0.4f, 0.0f);

    public Camera() {
        cameraPos = new Vector3f(0, 0, 1.0f);
    }

    /**
     * Given the time delta from the last frame, the camera position is updated using the controls.
     * 
     * @param delta time since last frame
     */
    public void updatePosition(long delta) {
        float lastFrameDuration = delta * 10 / TIME_SCALING;

        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                camTarget.z = camTarget.z - MOVEMENT_SPEED_SLOW * lastFrameDuration;
            } else {
                camTarget.z = camTarget.z - MOVEMENT_SPEED_FAST * lastFrameDuration;
            }
        } else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                camTarget.z = camTarget.z + MOVEMENT_SPEED_SLOW * lastFrameDuration;
            } else {
                camTarget.z = camTarget.z + MOVEMENT_SPEED_FAST * lastFrameDuration;
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                camTarget.x = camTarget.x + MOVEMENT_SPEED_SLOW * lastFrameDuration;
            } else {
                camTarget.x = camTarget.x + MOVEMENT_SPEED_FAST * lastFrameDuration;
            }
        } else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                camTarget.x = camTarget.x - MOVEMENT_SPEED_SLOW * lastFrameDuration;
            } else {
                camTarget.x = camTarget.x - MOVEMENT_SPEED_FAST * lastFrameDuration;
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                camTarget.y = camTarget.y - MOVEMENT_SPEED_SLOW * lastFrameDuration;
            } else {
                camTarget.y = camTarget.y - MOVEMENT_SPEED_FAST * lastFrameDuration;
            }
        } else if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                camTarget.y = camTarget.y + MOVEMENT_SPEED_SLOW * lastFrameDuration;
            } else {
                camTarget.y = camTarget.y + MOVEMENT_SPEED_FAST * lastFrameDuration;
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_I)) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                sphereCamRelPos.y = sphereCamRelPos.y - ROTATION_SPEED_FAST * lastFrameDuration;
            } else {
                sphereCamRelPos.y = sphereCamRelPos.y - ROTATION_SPEED_SLOW * lastFrameDuration;
            }
        } else if (Keyboard.isKeyDown(Keyboard.KEY_K)) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                sphereCamRelPos.y = sphereCamRelPos.y + ROTATION_SPEED_FAST * lastFrameDuration;
            } else {
                sphereCamRelPos.y = sphereCamRelPos.y + ROTATION_SPEED_SLOW * lastFrameDuration;
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_J)) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                sphereCamRelPos.x = sphereCamRelPos.x - ROTATION_SPEED_FAST * lastFrameDuration;
            } else {
                sphereCamRelPos.x = sphereCamRelPos.x - ROTATION_SPEED_SLOW * lastFrameDuration;
            }
        } else if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                sphereCamRelPos.x = sphereCamRelPos.x + ROTATION_SPEED_FAST * lastFrameDuration;
            } else {
                sphereCamRelPos.x = sphereCamRelPos.x + ROTATION_SPEED_SLOW * lastFrameDuration;
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                sphereCamRelPos.z = sphereCamRelPos.z - LOWERING_SPEED_FAST * lastFrameDuration;
            } else {
                sphereCamRelPos.z = sphereCamRelPos.z - LOWERING_SPEED_SLOW * lastFrameDuration;
            }
        } else if (Keyboard.isKeyDown(Keyboard.KEY_U)) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                sphereCamRelPos.z = sphereCamRelPos.z + LOWERING_SPEED_FAST * lastFrameDuration;
            } else {
                sphereCamRelPos.z = sphereCamRelPos.z + LOWERING_SPEED_SLOW * lastFrameDuration;
            }
        }

        camTarget.y = camTarget.y > 0.0f ? camTarget.y : 0.0f;
        sphereCamRelPos.z = sphereCamRelPos.z > 5.0f ? sphereCamRelPos.z : 5.0f;

    }

    /**
     * Check if given key is used for camera movement.
     * 
     * @param eventKey to check
     * @return true, if key is used in camera
     */
    public boolean isCameraKey(int eventKey) {
        return (eventKey == Keyboard.KEY_W || eventKey == Keyboard.KEY_S || eventKey == Keyboard.KEY_D
            || eventKey == Keyboard.KEY_A || eventKey == Keyboard.KEY_E || eventKey == Keyboard.KEY_Q
            || eventKey == Keyboard.KEY_I || eventKey == Keyboard.KEY_J || eventKey == Keyboard.KEY_O
            || eventKey == Keyboard.KEY_U || eventKey == Keyboard.KEY_L || eventKey == Keyboard.KEY_K);
    }

    private static float degToRad(float angDeg) {
        final float degToRad = (float) (Math.PI * 2.0f / DEGREES_360);
        return angDeg * degToRad;
    }

    /**
     * 
     * @return current camera position.
     */
    public Vector3f resolveCamPosition() {
        float phi = degToRad(sphereCamRelPos.x);
        float theta = degToRad(sphereCamRelPos.y + DEGREES_360 / 4.0f);

        float sinTheta = (float) Math.sin(theta);
        float cosTheta = (float) Math.cos(theta);
        float cosPhi = (float) Math.cos(phi);
        float sinPhi = (float) Math.sin(phi);

        Vector3f dirToCamera = new Vector3f(sinTheta * cosPhi, cosTheta, sinTheta * sinPhi);
        return Vector3f.add((Vector3f) dirToCamera.scale(sphereCamRelPos.z), camTarget, dirToCamera);
    }

    /**
     * Given the correct vectors, the matrix for the camera looking at is calculated.
     * 
     * @param cameraPt camera point
     * @param lookPt look at point
     * @param upPt vector up
     * @return look at matrix
     */
    public Matrix4f calcLookAtMatrix(Vector3f cameraPt, Vector3f lookPt, Vector3f upPt) {
        Vector3f lookDir = Vector3f.sub(lookPt, cameraPt, null).normalise(null);

        Vector3f upDir = upPt.normalise(null);

        Vector3f rightDir = Vector3f.cross(lookDir, upDir, null).normalise(null);
        Vector3f perpUpDir = Vector3f.cross(rightDir, lookDir, null);

        Matrix4f rotMat = new Matrix4f();
        rotMat.setIdentity();

        rotMat.m00 = rightDir.x;
        rotMat.m01 = rightDir.y;
        rotMat.m02 = rightDir.z;
        rotMat.m03 = 0;

        rotMat.m10 = perpUpDir.x;
        rotMat.m11 = perpUpDir.y;
        rotMat.m12 = perpUpDir.z;
        rotMat.m13 = 0;

        rotMat.m20 = -lookDir.x;
        rotMat.m21 = -lookDir.y;
        rotMat.m22 = -lookDir.z;
        rotMat.m23 = 0;

        rotMat.transpose(rotMat);

        Matrix4f transMat = new Matrix4f();
        transMat.setIdentity();

        transMat.m30 = -cameraPt.x;
        transMat.m31 = -cameraPt.y;
        transMat.m32 = -cameraPt.z;
        transMat.m33 = 1.0f;

        Matrix4f m = Matrix4f.mul(rotMat, transMat, null);
        return m;
    }

    public Vector3f getCamTarget() {
        return camTarget;
    }

    public void setPostion(Vector3f newPos) {
        camTarget = newPos;
    }

    public Vector3f getPosition() {
        return cameraPos;
    }

}
