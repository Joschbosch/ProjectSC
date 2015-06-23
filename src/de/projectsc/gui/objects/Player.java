/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.gui.objects;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.gui.models.TexturedModel;
import de.projectsc.gui.terrain.Terrain;

/**
 * Class for the player character, a special {@link Entity}.
 * 
 * @author Josch Bosch
 */
public class Player extends Entity {

    private static final float SECONDS_1000_0F = 1000.0f;

    private static final int FAST_MOVEMENT_SPEED_FACTOR = 10;

    private static final float MOVEMENT_SPEED = 20f;

    private static final float TURN_SPEED = 160f;

    private static final float GRAVITY = -50;

    private static final float JUMP_POWER = 30;

    private float currentSpeed = 0;

    private float currentTurnSpeed = 0;

    private float upwardsSpeed = 0;

    private boolean jumping = false;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    /**
     * Move the player.
     * 
     * @param delta elapsed time
     * @param terrain to render
     */
    public void move(float delta, Terrain terrain) {
        delta = (delta / SECONDS_1000_0F);
        checkInputs();
        super.increaseRotation(0, currentTurnSpeed * delta, 0);
        float distance = currentSpeed * delta;
        float dx = (float) (distance * Math.sin(Math.toRadians(getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(getRotY())));
        increasePostion(dx, 0, dz);
        upwardsSpeed += GRAVITY * delta;
        increasePostion(0, upwardsSpeed * delta, 0);
        float terrainHeight = terrain.getHeightOfTerrain(getPosition().x, getPosition().z);
        if (getPosition().y < terrainHeight) {
            upwardsSpeed = 0;
            getPosition().y = terrainHeight;
            jumping = false;
        }
    }

    private void jump() {
        if (!jumping) {
            upwardsSpeed = JUMP_POWER;
            jumping = true;
        }
    }

    private void checkInputs() {
        float movementSpeed = MOVEMENT_SPEED;
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            movementSpeed *= FAST_MOVEMENT_SPEED_FACTOR;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            currentSpeed = movementSpeed;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            currentSpeed = -movementSpeed;
        } else {
            currentSpeed = 0;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            currentTurnSpeed = -TURN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            currentTurnSpeed = TURN_SPEED;
        } else {
            currentTurnSpeed = 0;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            jump();
        }
    }
}
