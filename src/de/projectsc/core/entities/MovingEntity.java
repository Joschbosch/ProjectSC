/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.core.entities;

import java.util.Queue;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.Timer;
import de.projectsc.core.Terrain;
import de.projectsc.core.Tile;

/**
 * An entity that can move. Should be subclassed.
 * 
 * @author Josch Bosch
 */
public class MovingEntity extends WorldEntity {

    private static final int MOVEMENT_SPEED = 30;

    private final float currentTurnSpeed = 0;

    private Vector3f nextTarget;

    private float currentSpeed = 0;

    private boolean moved;

    private Queue<Tile> path;

    public MovingEntity(String model, String texture, Vector3f position, Vector3f rotation, float scale) {
        super(EntityType.MOVEABLE_OBJECT, model, texture, position, rotation, scale);
        moved = false;
        nextTarget = position;
    }

    public MovingEntity(Integer id, String model, String texture, Vector3f position, Vector3f rotation, float scale) {
        super(id, EntityType.MOVEABLE_OBJECT, model, texture, position, rotation, scale);
        moved = false;
        nextTarget = position;
    }

    /**
     * Move the entity.
     * 
     * @param delta elapsed time
     */
    public void move(float delta) {
        delta = (delta / Timer.SECONDS_CONSTANT);
        increaseRotation(0, currentTurnSpeed * delta, 0);
        if (Vector3f.sub(getPosition(), getCurrentTarget(), null).lengthSquared() > 2) {
            currentSpeed = MOVEMENT_SPEED;
        } else {
            path.poll();
            if (path.size() > 0) {
                setCurrentTarget(path.peek().getCoordinates());
            } else {
                currentSpeed = 0;
            }
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

    /**
     * Sets a target position for the entity.
     * 
     * @param currentTarget position
     */
    public void setCurrentTarget(Vector3f currentTarget) {
        this.nextTarget = currentTarget;
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

    public void setNewPath(Queue<Tile> path) {
        this.path = path;
        this.setCurrentTarget(path.peek().getCoordinates());

    }

    private void setCurrentTarget(Vector2f coordinates) {
        this.setCurrentTarget(new Vector3f(path.peek().getCoordinates().x * Terrain.TERRAIN_TILE_SIZE + Terrain.TERRAIN_TILE_SIZE / 2.0f,
            0, path.peek().getCoordinates().y * Terrain.TERRAIN_TILE_SIZE + Terrain.TERRAIN_TILE_SIZE / 2.0f));
    }

    public Vector3f getCurrentTarget() {
        return nextTarget;
    }

    /**
     * Return if the entity has moved in the current tick.
     * 
     * @return true, if it did
     */
    @Override
    public boolean hasMoved() {
        return moved;
    }

    public void setMoved(boolean value) {
        moved = value;
    }

    @Override
    public boolean isMovable() {
        return true;
    }

}
