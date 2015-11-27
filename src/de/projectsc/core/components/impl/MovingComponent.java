/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.components.impl;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.codehaus.jackson.JsonNode;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.models.RawModel;
import de.projectsc.client.gui.models.TexturedModel;
import de.projectsc.client.gui.objects.Billboard;
import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.objects.Light;
import de.projectsc.client.gui.objects.ParticleEmitter;
import de.projectsc.core.Tile;
import de.projectsc.core.components.Component;
import de.projectsc.core.components.ComponentType;
import de.projectsc.core.entities.Entity;
import de.projectsc.core.utils.BoundingBox;

/**
 * Entity component to allow movement.
 * 
 * @author Josch Bosch
 */
public class MovingComponent extends Component {

    /**
     * Name.
     */
    public static final String NAME = "Moving Component";

    private int turnSpeed = 60;

    private int movementSpeed = 30;

    private Vector3f nextTarget;

    private float currentSpeed = 0;

    private boolean moved = false;

    private Queue<Tile> path;

    // private float targetRotation;

    public MovingComponent(Entity owner) {
        super(NAME, owner);
        type = ComponentType.PREPHYSICS;
    }

    /**
     * Rotates the entity for the given deltas around the axis.
     * 
     * @param e entity to rotate
     * @param dx rotate on x axis
     * @param dy rotate on y axis
     * @param dz rotate on z axis
     */
    public void increaseRotation(Entity e, float dx, float dy, float dz) {
        e.setRotation(new Vector3f(e.getRotX() + dx, e.getRotY() + dy, e.getRotZ() + dz));
    }

    /**
     * Sets a target position for the entity.
     * 
     * @param e owner Entity
     * @param currentTarget position
     */
    public void setCurrentTarget(Entity e, Vector3f currentTarget) {
        this.nextTarget = currentTarget;
        Vector3f sub = Vector3f.sub(currentTarget, e.getPosition(), null);
        if (sub.length() != 0) {
            float angle = Vector3f.angle(sub, new Vector3f(0, 0, 1));
            float rotationTarget = (float) Math.toDegrees(angle);
            if (currentTarget.x < e.getPosition().x) {
                rotationTarget = -rotationTarget;
            }
            // this.targetRotation = rotationTarget;
        }
    }

    @Override
    public void update(Entity owner) {
        long delta = 15;
        // if (!(Math.abs((targetRotation - owner.getRotY())) < 1)) {
        // if (targetRotation > owner.getRotY()) {
        // increaseRotation(owner, 0f, TURN_SPEED * delta, 0f);
        // } else {
        // increaseRotation(owner, 0f, -TURN_SPEED * delta, 0f);
        // }
        // } else {
        // }
        if (nextTarget != null && Vector3f.sub(owner.getPosition(), nextTarget, null).lengthSquared() > 2) {
            currentSpeed = movementSpeed;
        } else {
            if (path != null) {
                path.poll();
                if (path.size() > 0) {
                    setCurrentTarget(owner, new Vector3f(path.peek().getCoordinates().x, 0, path.peek().getCoordinates().y));
                } else {
                    currentSpeed = 0;
                }
            }
        }
        float distance = currentSpeed * delta / 1000.0f;
        float dx = (float) (distance * Math.sin(Math.toRadians(owner.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(owner.getRotY())));
        increasePostion(owner, dx, 0, dz);
        if (dx != 0 || dz != 0) {
            moved = true;
        } else {
            moved = false;
        }
    }

    @Override
    public void render(Entity owner, Map<TexturedModel, List<Entity>> entities, Map<RawModel, List<BoundingBox>> boundingBoxes,
        List<Light> lights, List<Billboard> billboards, List<ParticleEmitter> particles, Camera camera, long elapsedTime) {}

    /**
     * Moves the entity with the given deltas.
     * 
     * @param e entity
     * @param dx x delta
     * @param dy y delta
     * @param dz z delta
     */
    public void increasePostion(Entity e, float dx, float dy, float dz) {
        e.getPosition().x += dx;
        e.getPosition().y += dy;
        e.getPosition().z += dz;
    }

    @Override
    public String serialize() {
        return "" + movementSpeed;
    }

    @Override
    public void deserialize(JsonNode input, File schemaDir) {
        movementSpeed = Integer.valueOf(input.getTextValue());
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public Vector3f getNextTarget() {
        return nextTarget;
    }

    public void setNextTarget(Vector3f nextTarget) {
        this.nextTarget = nextTarget;
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(float currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public int getTurnSpeed() {
        return turnSpeed;
    }

    public void setTurnSpeed(int turnSpeed) {
        this.turnSpeed = turnSpeed;
    }

    public void setMovementSpeed(int movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public int getMovementSpeed() {
        return movementSpeed;
    }

    @Override
    public boolean isValidForSaving() {
        return true;
    }

}
