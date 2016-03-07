/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui.objects;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.interfaces.Entity;
import de.projectsc.modes.client.core.data.KeyboardInputCommand;
import de.projectsc.modes.client.core.data.MouseInputCommand;
import de.projectsc.modes.client.core.interfaces.InputCommandListener;

/**
 * Class for moving around in the world.
 * 
 * @author Josch Bosch
 */
public class Camera implements InputCommandListener {

    protected static final int DEGREES_180 = 180;

    protected static final int CONSTANT_DISTANCE_FROM_ENTITY = 80;

    protected static final int MINIMUM_Y_POSITION = 50;

    protected static final int MAXIMUM_Y_POSITION = 80;

    protected static final boolean FIXED_MODE = true;

    protected static final float ANGLE_AROUND_PLAYER_FACTOR = 0.3f;

    protected static final float PITCH_FACTOR = 0.1f;

    protected static final float MOUSE_WHEEL_ZOOM_FACTOR = 0.005f;

    protected static final int MAXIMUM_PITCH_ANGLE = 90;

    protected static final float MINIMUM_PITCH_ANGLE = -25.0f;

    protected static final int MAX_DISTANCE_TO_PLAYER = 100;

    protected static final int FAST_MOVEMENT_SPEED_FACTOR = 5;

    protected static final float MOVEMENT_SPEED = 60f;

    protected static final int SCROLL_MARGIN = 2;

    protected static final boolean NO_CAMERA_MOVING = false;

    protected boolean bound;

    protected float distanceFromCenterPoint = 3 * 10f;

    protected float yaw = 0;

    protected float pitch = 5 * 10;

    protected float roll = 0;

    protected float angleAroundPlayer = 0;

    protected final Vector3f position = new Vector3f(0, 80f, 0);

    protected Vector3f centeringPoint = new Vector3f(0, 0, 0);

    protected Matrix4f viewMatrix;

    private float currentSpeedXKeys;

    private float currentSpeedZKeys;

    private float currentSpeedXMouse;

    private float currentSpeedZMouse;

    private int mouseWheel = 0;

    private int mouseDX;

    private int mouseDY;

    private boolean mouseButton1;

    private boolean mouseButton0;

    private boolean moveLeft;

    private boolean moveRight;

    private int movementFactor = 1;

    private boolean moveUp;

    private boolean moveDown;

    private boolean consumeInput;

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
        if (!FIXED_MODE) {
            calculateZoom();
            calculatePitch();
            calculateAngleAroundPlayer();
            float horizontalDistance = calculateHorizontalDistance();
            float verticalDistance = calculateVerticalDistance();
            calculateCameraPosition(centeringPoint, horizontalDistance, verticalDistance);
            this.yaw = DEGREES_180 - (0 + angleAroundPlayer);
        } else {
            if (!bound) {
                calculateZoom();
                calculateCameraPosition(delta);
            } else {
                float horizontalDistance = calculateHorizontalDistance();
                float verticalDistance = calculateVerticalDistance();
                calculateCameraPosition(centeringPoint, horizontalDistance, verticalDistance);
            }
        }
        viewMatrix = createViewMatrix();
    }

    @Override
    public InputConsumeLevel getInputConsumeLevel() {
        return InputConsumeLevel.SECOND;
    }

    @Override
    public void handleKeyboardCommand(KeyboardInputCommand command) {
        if (consumeInput) {
            if (command.isShiftDown()) {
                movementFactor = FAST_MOVEMENT_SPEED_FACTOR;
            } else {
                movementFactor = 1;
            }
            if (command.getKey() == Keyboard.KEY_A) {
                moveLeft = command.isKeyDown();
                command.consume();
            } else if (command.getKey() == Keyboard.KEY_D) {
                moveRight = command.isKeyDown();
                command.consume();
            }
            if (command.getKey() == Keyboard.KEY_W) {
                moveUp = command.isKeyDown();
                command.consume();
            } else if (command.getKey() == Keyboard.KEY_S) {
                moveDown = command.isKeyDown();
                command.consume();
            }
        }

    }

    @Override
    public void handleMouseCommand(MouseInputCommand command) {
        currentSpeedXMouse = 0;
        currentSpeedZMouse = 0;
        float movementSpeed = MOVEMENT_SPEED;
        if (isBetween(command.getMouseX(), 0, SCROLL_MARGIN)) {
            currentSpeedXMouse = -FAST_MOVEMENT_SPEED_FACTOR * movementSpeed;
        } else if (isBetween(command.getMouseX(), Display.getWidth() - SCROLL_MARGIN, Display.getWidth())) {
            currentSpeedXMouse = FAST_MOVEMENT_SPEED_FACTOR * movementSpeed;
        } else if (isBetween(command.getMouseX(), SCROLL_MARGIN, 2 * SCROLL_MARGIN)) {
            currentSpeedXMouse = -movementSpeed;
        } else if (isBetween(command.getMouseX(), Display.getWidth() - 2 * SCROLL_MARGIN, Display.getWidth() - SCROLL_MARGIN)) {
            currentSpeedXMouse = movementSpeed;
        }
        if (isBetween(command.getMouseY(), 0, SCROLL_MARGIN)) {
            currentSpeedZMouse = FAST_MOVEMENT_SPEED_FACTOR * movementSpeed;
        } else if (isBetween(command.getMouseY(), Display.getHeight() - SCROLL_MARGIN, Display.getWidth())) {
            currentSpeedZMouse = -FAST_MOVEMENT_SPEED_FACTOR * movementSpeed;
        } else if (isBetween(command.getMouseY(), SCROLL_MARGIN, 2 * SCROLL_MARGIN)) {
            currentSpeedZMouse = movementSpeed;
        } else if (isBetween(command.getMouseY(), Display.getHeight() - 2 * SCROLL_MARGIN, Display.getWidth() - SCROLL_MARGIN)) {
            currentSpeedZMouse = -movementSpeed;
        }
        this.mouseWheel = command.getMouseWheel();
        this.mouseDX = command.getMouseDX();
        this.mouseDY = command.getMouseDY();
        if (command.getButton() == 0) {
            this.mouseButton0 = command.isButtonDown(0);
        }
        if (command.getButton() == 1) {
            this.mouseButton1 = command.isButtonDown(1);
        }
    }

    protected boolean isBetween(float value, int lower, int upper) {
        return value <= upper && value >= lower;
    }

    protected void calculateCameraPosition(float delta) {
        currentSpeedXKeys = 0;
        currentSpeedZKeys = 0;
        if (moveLeft) {
            currentSpeedXKeys -= movementFactor * MOVEMENT_SPEED;
        }
        if (moveRight) {
            currentSpeedXKeys += movementFactor * MOVEMENT_SPEED;
        }
        if (moveUp) {
            currentSpeedZKeys -= movementFactor * MOVEMENT_SPEED;
        }
        if (moveDown) {
            currentSpeedZKeys += movementFactor * MOVEMENT_SPEED;
        }
        float currentSpeedX = currentSpeedXKeys + currentSpeedXMouse;
        float currentSpeedZ = currentSpeedZKeys + currentSpeedZMouse;
        centeringPoint.x = centeringPoint.x + delta / 1000.0f * currentSpeedX;
        centeringPoint.z = centeringPoint.z + delta / 1000.0f * currentSpeedZ;
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
        Matrix4f newViewMatrix = new Matrix4f();
        newViewMatrix.setIdentity();
        Matrix4f.rotate(((float) Math.toRadians(pitch)), new Vector3f(1, 0, 0), newViewMatrix, newViewMatrix);
        Matrix4f.rotate(((float) Math.toRadians(yaw)), new Vector3f(0, 1, 0), newViewMatrix, newViewMatrix);
        Matrix4f.rotate(((float) Math.toRadians(roll)), new Vector3f(0, 0, 1), newViewMatrix, newViewMatrix);

        Vector3f negCameraPos = new Vector3f(-position.x, -position.y, -position.z);
        Matrix4f.translate(negCameraPos, newViewMatrix, newViewMatrix);
        return newViewMatrix;
    }

    protected void calculateZoom() {
        if (!FIXED_MODE) {
            float zoomLevel = mouseWheel * MOUSE_WHEEL_ZOOM_FACTOR;
            distanceFromCenterPoint -= zoomLevel;
            if (distanceFromCenterPoint < 0) {
                distanceFromCenterPoint = 0;
            } else if (distanceFromCenterPoint > MAX_DISTANCE_TO_PLAYER) {
                distanceFromCenterPoint = MAX_DISTANCE_TO_PLAYER;
            }
        } else {
            float zoomLevel = mouseWheel * MOUSE_WHEEL_ZOOM_FACTOR;
            if (position.y <= MAXIMUM_Y_POSITION && zoomLevel < 0 || position.y >= MINIMUM_Y_POSITION && zoomLevel > 0) {
                position.y -= zoomLevel;
                pitch -= zoomLevel;
                position.z -= zoomLevel;
            }
        }
        mouseWheel = 0;
    }

    protected void calculatePitch() {
        if (mouseButton1) {
            float pitchChange = mouseDY * PITCH_FACTOR;
            pitch -= pitchChange;
        }
        if (pitch < MINIMUM_PITCH_ANGLE) {
            pitch = MINIMUM_PITCH_ANGLE;
        } else if (pitch > MAXIMUM_PITCH_ANGLE) {
            pitch = MAXIMUM_PITCH_ANGLE;
        }
    }

    protected void calculateAngleAroundPlayer() {
        if (mouseButton0) {
            float angleChange = mouseDX * ANGLE_AROUND_PLAYER_FACTOR;
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
            // centeringPoint = entity.getTransform().getPosition();
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

    public void setConsumeInput(boolean cameraMoveable) {
        this.consumeInput = cameraMoveable;
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

    /**
     * lazy init getter.
     * 
     * @return current view matrix
     */
    public Matrix4f getViewMatrix() {
        if (viewMatrix == null) {
            createViewMatrix();
        }
        return viewMatrix;
    }
}
