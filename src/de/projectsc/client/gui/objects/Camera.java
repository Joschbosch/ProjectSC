/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.gui.objects;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Class for moving around in the world.
 * 
 * @author Josch Bosch
 */
public class Camera {

    private static final int CONSTANT_DISTANCE_FROM_ENTITY = 80;

    private static final int MINIMUM_Y_POSITION = 50;

    private static final int MAXIMUM_Y_POSITION = 80;

    private static final boolean MOBA_MODE = true;

    private static final float ANGLE_AROUND_PLAYER_FACTOR = 0.3f;

    private static final float PITCH_FACTOR = 0.1f;

    private static final float MOUSE_WHEEL_ZOOM_FACTOR = 0.05f;

    private static final int MAXIMUM_PITCH_ANGLE = 90;

    private static final float MINIMUM_PITCH_ANGLE = -25.0f;

    private static final int MAX_DISTANCE_TO_PLAYER = 100;

    // private static final float PLAYER_CENTER_Y_AXIS = 15.5f;

    private static final int DEGREES_180 = 180;

    private static final int FAST_MOVEMENT_SPEED_FACTOR = 5;

    private static final float MOVEMENT_SPEED = 60f;

    private static final int SCROLL_MARGIN = 15;

    private final Vector3f position = new Vector3f(0, 80f, 0);

    private float pitch = 5 * 10;

    private float yaw = 0;

    private float roll = 0;

    private final GraphicalEntity player;

    private float distanceFromPlayer = 3 * 10f;

    private float angleAroundPlayer = 0;

    private float currentSpeedX;

    private float currentSpeedZ;

    private GraphicalEntity boundToEntity = null;

    private final Object entityLockObject = new Object();

    public Camera(GraphicalEntity player) {
        this.player = player;
        if (player != null) {
            position.x = player.getPosition().x;
            position.z = player.getPosition().z + distanceFromPlayer;
        }
    }

    /**
     * Get keys and move camera.
     * 
     * @param delta elapsed time.
     */
    public void move(float delta) {
        if (!MOBA_MODE) {
            calculateZoom();
            calculatePitch();
            calculateAngleAroundPlayer();
            float horizontalDistance = calculateHorizontalDistance();
            float verticalDistance = calculateVerticalDistance();
            calculateCameraPosition(horizontalDistance, verticalDistance);
            this.yaw = DEGREES_180 - (player.getRotY() + angleAroundPlayer);
        } else {
            synchronized (entityLockObject) {
                checkInputs();
                if (boundToEntity == null) {
                    calculateZoom();
                    calculateCameraPosition(delta);
                } else {
                    float horizontalDistance = calculateHorizontalDistance();
                    float verticalDistance = calculateVerticalDistance();
                    calculateCameraPosition(horizontalDistance, verticalDistance);
                }
            }
        }
    }

    private void checkInputs() {
        float movementSpeed = MOVEMENT_SPEED;
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            movementSpeed *= FAST_MOVEMENT_SPEED_FACTOR;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            currentSpeedX = -movementSpeed;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            currentSpeedX = movementSpeed;
        } else {
            currentSpeedX = 0;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            currentSpeedZ = -movementSpeed;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            currentSpeedZ = movementSpeed;
        } else {
            currentSpeedZ = 0;
        }
        if (isBetween(Mouse.getX(), 0, SCROLL_MARGIN)) {
            currentSpeedX = -FAST_MOVEMENT_SPEED_FACTOR * movementSpeed;
        } else if (isBetween(Mouse.getX(), Display.getWidth() - SCROLL_MARGIN, Display.getWidth())) {
            currentSpeedX = FAST_MOVEMENT_SPEED_FACTOR * movementSpeed;
        } else if (isBetween(Mouse.getX(), SCROLL_MARGIN, 2 * SCROLL_MARGIN)) {
            currentSpeedX = -movementSpeed;
        } else if (isBetween(Mouse.getX(), Display.getWidth() - 2 * SCROLL_MARGIN, Display.getWidth() - SCROLL_MARGIN)) {
            currentSpeedX = movementSpeed;
        }
        if (isBetween(Mouse.getY(), 0, SCROLL_MARGIN)) {
            currentSpeedZ = FAST_MOVEMENT_SPEED_FACTOR * movementSpeed;
        } else if (isBetween(Mouse.getY(), Display.getHeight() - SCROLL_MARGIN, Display.getWidth())) {
            currentSpeedZ = -FAST_MOVEMENT_SPEED_FACTOR * movementSpeed;
        } else if (isBetween(Mouse.getY(), SCROLL_MARGIN, 2 * SCROLL_MARGIN)) {
            currentSpeedZ = movementSpeed;
        } else if (isBetween(Mouse.getY(), Display.getHeight() - 2 * SCROLL_MARGIN, Display.getWidth() - SCROLL_MARGIN)) {
            currentSpeedZ = -movementSpeed;
        }
    }

    private boolean isBetween(float value, int lower, int upper) {
        return value <= upper && value >= lower;
    }

    private void calculateCameraPosition(float delta) {
        position.x = position.x + delta / 1000.0f * currentSpeedX;
        position.z = position.z + delta / 1000.0f * currentSpeedZ;
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        position.x = boundToEntity.getPosition().x;
        position.z = boundToEntity.getPosition().z + CONSTANT_DISTANCE_FROM_ENTITY;
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
        if (!MOBA_MODE) {
            float zoomLevel = Mouse.getDWheel() * MOUSE_WHEEL_ZOOM_FACTOR;
            distanceFromPlayer -= zoomLevel;
            if (distanceFromPlayer < 0) {
                distanceFromPlayer = 0;
            } else if (distanceFromPlayer > MAX_DISTANCE_TO_PLAYER) {
                distanceFromPlayer = MAX_DISTANCE_TO_PLAYER;
            }
        } else {
            float zoomLevel = Mouse.getDWheel() * MOUSE_WHEEL_ZOOM_FACTOR;
            if (position.y <= MAXIMUM_Y_POSITION && zoomLevel < 0 || position.y >= MINIMUM_Y_POSITION && zoomLevel > 0) {
                position.y -= zoomLevel;
                pitch -= zoomLevel;
                position.z -= zoomLevel;
            }
        }
    }

    private void calculatePitch() {
        if (Mouse.isButtonDown(1)) {
            float pitchChange = Mouse.getDY() * PITCH_FACTOR;
            pitch -= pitchChange;
        }
        if (pitch < MINIMUM_PITCH_ANGLE) {
            pitch = MINIMUM_PITCH_ANGLE;
        } else if (pitch > MAXIMUM_PITCH_ANGLE) {
            pitch = MAXIMUM_PITCH_ANGLE;
        }
    }

    private void calculateAngleAroundPlayer() {
        if (Mouse.isButtonDown(0)) {
            float angleChange = Mouse.getDX() * ANGLE_AROUND_PLAYER_FACTOR;
            angleAroundPlayer -= angleChange;
        }
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    /**
     * Inverts current pitch of the camera (e.g. used for water reflection).
     */
    public void invertPitch() {
        pitch = -pitch;
    }

    /**
     * Bind camera to world entity and follow it.
     * 
     * @param entity to bind to.
     */
    public void bindToEntity(GraphicalEntity entity) {
        synchronized (entityLockObject) {
            if (boundToEntity == null) {
                boundToEntity = entity;
            } else {
                boundToEntity = null;
            }
        }
    }
}
