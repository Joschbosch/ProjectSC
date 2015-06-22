/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.objects;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Class for moving around in the world.
 * 
 * @author Josch Bosch
 */
public class Camera {

    private static final int MAX_DISTANCE_TO_PLAYER = 100;

    private static final float PLAYER_CENTER_Y_AXIS = 15.5f;

    private static final int DEGREES_180 = 180;

    private final Vector3f position = new Vector3f(0, 0.5f, 0);

    private float pitch = 20;

    private float yaw = 10;

    private float roll = 0;

    private final Player player;

    private float distanceFromPlayer = 30f;

    private float angleAroundPlayer = 0;

    public Camera(Player player) {
        this.player = player;
    }

    /**
     * Get keys and move camera.
     */
    public void move() {
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = DEGREES_180 - (player.getRotY() + angleAroundPlayer);
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + verticalDistance + PLAYER_CENTER_Y_AXIS;

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

    private void calculateZoom() {
        float zoomLevel = Mouse.getDWheel() * 0.01f;
        distanceFromPlayer -= zoomLevel;
        if (distanceFromPlayer < 0) {
            distanceFromPlayer = 0;
        } else if (distanceFromPlayer > MAX_DISTANCE_TO_PLAYER) {
            distanceFromPlayer = MAX_DISTANCE_TO_PLAYER;
        }
    }

    private void calculatePitch() {
        if (Mouse.isButtonDown(1)) {
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
        }
        if (pitch < -25.0) {
            pitch = -25.0f;
        } else if (pitch > 90) {
            pitch = 90;
        }
    }

    private void calculateAngleAroundPlayer() {
        if (Mouse.isButtonDown(0)) {
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;
        }
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }
}
