/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.Timer;
import de.projectsc.core.utils.BoundingBox;
import de.projectsc.core.utils.BoundingBoxLoader;

/**
 * An entity in the world.
 * 
 * @author Josch Bosch
 */
public class WorldEntity implements Comparable<WorldEntity> {

    // private static final float TURN_SPEED = 160f;
    //
    // private static final float GRAVITY = -50;

    // private static final float JUMP_POWER = 30;

    private static final int MOVEMENT_SPEED = 30;

    private static final Log LOGGER = LogFactory.getLog(WorldEntity.class);

    private static int idCount = 0;

    private final EntityType type;

    private Vector3f position;

    private final Vector3f rotation;

    private float scale;

    private Vector3f currentTarget;

    private float currentSpeed = 0;

    private final float currentTurnSpeed = 0;

    private final int id;

    private String model;

    private String texture;

    private BoundingBox boundingBox;

    private boolean moved;

    public WorldEntity(EntityType type, String model, String texture, Vector3f position, Vector3f rotation, float scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.setCurrentTarget(position);
        this.id = idCount++;
        this.type = type;
        this.setModel(model);
        this.setTexture(texture);
        boundingBox = BoundingBoxLoader.readBoundingBox(this);
        moved = false;
    }

    public WorldEntity(int id, EntityType type, String model, String texture, Vector3f position, Vector3f rotation, float scale) {
        this.id = id;
        if (idCount < id) {
            idCount = id + 1;
        }
        this.position = position;
        this.rotation = rotation;
        this.type = type;
        this.setCurrentTarget(position);
        this.scale = scale;
        this.setModel(model);
        this.setTexture(texture);
        boundingBox = BoundingBoxLoader.readBoundingBox(this);
        moved = false;
    }

    /**
     * Move the entity.
     * 
     * @param delta elapsed time
     */
    public void move(float delta) {
        delta = (delta / Timer.SECONDS_CONSTANT);
        increaseRotation(0, currentTurnSpeed * delta, 0);
        if (Vector3f.sub(getPosition(), getCurrentTarget(), null).lengthSquared() > 3) {
            currentSpeed = MOVEMENT_SPEED;
        } else {
            currentSpeed = 0;
        }
        float distance = currentSpeed * delta;
        float dx = (float) (distance * Math.sin(Math.toRadians(getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(getRotY())));
        increasePostion(dx, 0, dz);
        if (dx != 0 || dz != 0) {
            moved = true;
        } else {
            moved = false;
        }
    }

    /**
     * Get next position of entity.
     * 
     * @param delta elapsed time
     * @return new position.
     */
    public Vector3f getNextPosition(float delta) {
        delta = (delta / Timer.SECONDS_CONSTANT);
        increaseRotation(0, currentTurnSpeed * delta, 0);
        if (Vector3f.sub(getPosition(), getCurrentTarget(), null).lengthSquared() > 3) {
            currentSpeed = MOVEMENT_SPEED;
        } else {
            currentSpeed = 0;
        }
        float distance = currentSpeed * delta;
        float dx = (float) (distance * Math.sin(Math.toRadians(getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(getRotY())));
        increasePostion(dx, 0, dz);
        return new Vector3f(position.x + dx, position.y, position.z + dz);
    }

    /**
     * Sets a target position for the entity.
     * 
     * @param currentTarget position
     */
    public void setCurrentTarget(Vector3f currentTarget) {
        this.currentTarget = currentTarget;
        Vector3f sub = Vector3f.sub(currentTarget, position, null);
        if (sub.length() != 0) {
            float angle = Vector3f.angle(sub, new Vector3f(0, 0, 1));
            float rotate = (float) Math.toDegrees(angle);
            if (currentTarget.x < position.x) {
                rotate = -rotate;
            }
            setRotY(rotate);
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
        setRotX(getRotX() + dx);
        setRotY(getRotY() + dy);
        setRotZ(getRotZ() + dz);
    }

    public Vector3f getLocationBoundingBoxMinimum() {
        return Vector3f.add(boundingBox.getMin(), position, null);
    }

    public Vector3f getLocationBoundingBoxMaximum() {
        return Vector3f.add(boundingBox.getMax(), position, null);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getRotX() {
        return rotation.x;
    }

    public void setRotX(float rotX) {
        this.rotation.x = rotX;
    }

    public float getRotY() {
        return rotation.y;
    }

    public void setRotY(float rotY) {
        this.rotation.y = rotY;
    }

    public float getRotZ() {
        return rotation.z;
    }

    public void setRotZ(float rotZ) {
        this.rotation.z = rotZ;
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

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    @Override
    public String toString() {
        String result = "";
        result += getModel() + ", ";
        result += getTexture() + ", ";
        result += getID() + ", ";
        result += getPosition() + ", ";
        result += getRotX() + ", ";
        result += getRotY() + ", ";
        result += getRotZ() + ", ";
        result += getScale() + ", ";
        result += getType() + "";
        return result;
    }

    public boolean hasMoved() {
        return moved;
    }

    public void setMoved(boolean value) {
        moved = value;
    }

    @Override
    public int compareTo(WorldEntity arg0) {
        return getID().compareTo(arg0.getID());
    }
}
