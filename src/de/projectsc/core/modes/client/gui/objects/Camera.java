/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.modes.client.gui.objects;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.entities.Entity;

/**
 * Class for moving around in the world.
 * 
 * @author Josch Bosch
 */
public class Camera {

    protected static final int DEGREES_180 = 180;

    protected static final int CONSTANT_DISTANCE_FROM_ENTITY = 80;

    protected static final int MINIMUM_Y_POSITION = 50;

    protected static final int MAXIMUM_Y_POSITION = 80;

    protected static final boolean MOBA_MODE = true;

    protected static final float ANGLE_AROUND_PLAYER_FACTOR = 0.3f;

    protected static final float PITCH_FACTOR = 0.1f;

    protected static final float MOUSE_WHEEL_ZOOM_FACTOR = 0.05f;

    protected static final int MAXIMUM_PITCH_ANGLE = 90;

    protected static final float MINIMUM_PITCH_ANGLE = -25.0f;

    protected static final int MAX_DISTANCE_TO_PLAYER = 100;

    protected static final int FAST_MOVEMENT_SPEED_FACTOR = 5;

    protected static final float MOVEMENT_SPEED = 60f;

    protected static final int SCROLL_MARGIN = 15;

    protected static final boolean NO_CAMERA_MOVING = false;

    protected float distanceFromCenterPoint = 3 * 10f;

    protected float yaw = 0;

    protected float angleAroundPlayer = 0;

    protected final Vector3f position = new Vector3f(0, 80f, 0);

    protected float pitch = 5 * 10;

    protected Vector3f centeringPoint = new Vector3f(0, 0, 0);

    protected float roll = 0;

    private float currentSpeedX;

    private float currentSpeedZ;

    private boolean bound;

    public Camera() {

    }

    /**
     * Get keys and move camera.
     * 
     * @param delta elapsed time.
     */
    public void move(float delta) {
        if (NO_CAMERA_MOVING) {
            return;
        }

        if (!MOBA_MODE) {
            calculateZoom();
            calculatePitch();
            calculateAngleAroundPlayer();
            float horizontalDistance = calculateHorizontalDistance();
            float verticalDistance = calculateVerticalDistance();
            calculateCameraPosition(centeringPoint, horizontalDistance, verticalDistance);
            this.yaw = DEGREES_180 - (0 + angleAroundPlayer);
        } else {

            checkInputs();
            if (!bound) {
                calculateZoom();
                calculateCameraPosition(delta);
            } else {
                float horizontalDistance = calculateHorizontalDistance();
                float verticalDistance = calculateVerticalDistance();
                calculateCameraPosition(centeringPoint, horizontalDistance, verticalDistance);
            }
        }
    }

    protected void checkInputs() {
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

    protected boolean isBetween(float value, int lower, int upper) {
        return value <= upper && value >= lower;
    }

    protected void calculateCameraPosition(float delta) {
        centeringPoint.x = centeringPoint.x + delta / 1000.0f * currentSpeedX;
        centeringPoint.z = centeringPoint.z + delta / 1000.0f * currentSpeedX;
        position.x = position.x + delta / 1000.0f * currentSpeedX;
        position.z = position.z + delta / 1000.0f * currentSpeedZ;
    }

    protected void calculateCameraPosition(Vector3f lookAtPoint, float horizontalDistance, float verticalDistance) {
        position.x = lookAtPoint.x;
        position.z = lookAtPoint.z + CONSTANT_DISTANCE_FROM_ENTITY;
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

    protected void calculateZoom() {
        if (!MOBA_MODE) {
            float zoomLevel = Mouse.getDWheel() * MOUSE_WHEEL_ZOOM_FACTOR;
            distanceFromCenterPoint -= zoomLevel;
            if (distanceFromCenterPoint < 0) {
                distanceFromCenterPoint = 0;
            } else if (distanceFromCenterPoint > MAX_DISTANCE_TO_PLAYER) {
                distanceFromCenterPoint = MAX_DISTANCE_TO_PLAYER;
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

    protected void calculatePitch() {
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

    protected void calculateAngleAroundPlayer() {
        if (Mouse.isButtonDown(0)) {
            float angleChange = Mouse.getDX() * ANGLE_AROUND_PLAYER_FACTOR;
            angleAroundPlayer -= angleChange;
        }
    }

    protected float calculateHorizontalDistance() {
        return (float) (distanceFromCenterPoint * Math.cos(Math.toRadians(pitch)));
    }

    protected float calculateVerticalDistance() {
        return (float) (distanceFromCenterPoint * Math.sin(Math.toRadians(pitch)));
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
    public void bindToEntity(Entity entity) {
        if (entity == null) {
            Vector3f newCenterPoint = new Vector3f(centeringPoint.x, centeringPoint.y, centeringPoint.z);
            centeringPoint = newCenterPoint;
            bound = false;
        } else {
            centeringPoint = entity.getPosition();
            bound = true;
        }
    }

    /**
     * @param x coord
     * @param y coord
     * @param z coord
     */
    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    /**
     * @param x coord
     * @param y coord
     * @param z coord
     */
    public void setLookAtPoint(float x, float y, float z) {
        centeringPoint.x = x;
        centeringPoint.y = y;
        centeringPoint.z = z;
    }
}
