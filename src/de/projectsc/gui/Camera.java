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
 * Class for moving around in the world.
 * 
 * @author Josch Bosch
 */
public class Camera {

    private static final int FAST_MOVEMENT_SPEED_FACTOR = 10;

    private static final float SLOW_MOVEMENT_SPEED = 0.02f;

    private final Vector3f position = new Vector3f(0, 0.5f, 0);

    private float pitch = 0;

    private float yaw = 0;

    private float roll = 0;

    public Camera() {}

    /**
     * Get keys and move camera.
     */
    public void move() {
        float movementSpeed = SLOW_MOVEMENT_SPEED;
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            movementSpeed *= FAST_MOVEMENT_SPEED_FACTOR;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            position.z -= movementSpeed;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            position.z += movementSpeed;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            position.x += movementSpeed;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            position.x -= movementSpeed;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
            position.y += movementSpeed;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
            position.y -= movementSpeed;
        }
    }

    /**
     * @return the current view matrix.
     */
    public Matrix4f createViewMatrix() {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
        Matrix4f.rotate(((float) Math.toRadians(pitch)), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
        Matrix4f.rotate(((float) Math.toRadians(yaw)), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        Matrix4f.rotate(((float) Math.toRadians(roll)), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);

        Vector3f negCameraPos = new Vector3f(-position.x, -position.y, -position.z);
        Matrix4f.translate(negCameraPos, viewMatrix, viewMatrix);
        return viewMatrix;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public Vector3f getPosition() {
        return position;
    }

}
