/*
 * Copyright (C) 2015 
 */

package de.projectsc.core;

import org.lwjgl.util.vector.Vector3f;

public class WorldEntity {

    private static final float SECONDS_1000_0F = 1000.0f;

    private static final float TURN_SPEED = 160f;

    private static final float GRAVITY = -50;

    private static final float JUMP_POWER = 30;

    private static int idCount = 0;

    private EntityType type;

    private Vector3f position;

    private float rotX;

    private float rotY;

    private float rotZ;

    private float scale;

    private Vector3f currentTarget;

    private float currentSpeed = 0;

    private float currentTurnSpeed = 0;

    private float upwardsSpeed = 0;

    private boolean jumping = false;

    private int id;

    private String model;

    private String texture;

    public WorldEntity(EntityType type, String model, String texture, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        this.position = position;
        this.setCurrentTarget(position);
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
        this.id = idCount++;
        this.setModel(model);
        this.setTexture(texture);
    }

    public WorldEntity(int id, EntityType type, String model, String texture, Vector3f position, float rotX, float rotY, float rotZ,
        float scale) {
        this.id = id;
        this.position = position;
        this.setCurrentTarget(position);
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
        this.setModel(model);
        this.setTexture(texture);
    }

    /**
     * Move the player.
     * 
     * @param delta elapsed time
     * @param terrain to render
     */
    public void move(float delta) {
        if (type == EntityType.PLAYER) {
            System.out.println(getPosition());
        }
        delta = (delta / SECONDS_1000_0F);
        increaseRotation(0, currentTurnSpeed * delta, 0);
        if (Vector3f.sub(getPosition(), getCurrentTarget(), null).lengthSquared() > 3) {
            currentSpeed = 20;
        } else {
            currentSpeed = 0;
        }
        float distance = currentSpeed * delta;
        float dx = (float) (distance * Math.sin(Math.toRadians(getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(getRotY())));
        increasePostion(dx, 0, dz);
    }

    public void setCurrentTarget(Vector3f currentTarget) {
        this.currentTarget = currentTarget;
        float rotate = (float) Math.toDegrees(Vector3f.angle(Vector3f.sub(currentTarget, position, null), new Vector3f(0, 0, 1)));
        if (currentTarget.x < position.x) {
            rotate = -rotate;
        }
        this.rotY = rotate;

    }

    private void jump() {
        if (!jumping) {
            upwardsSpeed = JUMP_POWER;
            jumping = true;
        }
    }

    /**
     * Moves the entity with the given deltas.
     * 
     * @param dx x delta
     * @param dy y delta
     * @param dz z delta
     */
    public void increasePostion(float dx, float dy, float dz) {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }

    /**
     * Rotates the entity for the given deltas around the axis.
     * 
     * @param dx rotate on x axis
     * @param dy rotate on y axis
     * @param dz rotate on z axis
     */
    public void increaseRotation(float dx, float dy, float dz) {
        this.rotX += dx;
        this.rotY += dy;
        this.rotZ += dz;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getRotX() {
        return rotX;
    }

    public void setRotX(float rotX) {
        this.rotX = rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public void setRotZ(float rotZ) {
        this.rotZ = rotZ;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getCurrentTarget() {
        return currentTarget;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public Integer getID() {
        return id;
    }

    public EntityType getType() {
        return type;
    }

}
