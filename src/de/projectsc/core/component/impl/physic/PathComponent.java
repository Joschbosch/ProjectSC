/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.component.impl.physic;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.entities.states.EntityState;
import de.projectsc.core.events.entities.ChangeEntityStateEvent;

public class PathComponent extends PhysicsComponent {

    public static final float DISTANCE_TO_TARGET = 2f;

    /**
     * ID.
     */
    public static final String NAME = "Path Component";

    private Vector3f currentTarget = null;

    private Vector3f targetRotation;

    public PathComponent() {
        setID(NAME);
        setType(ComponentType.PHYSICS);
    }

    @Override
    public void update(long elapsed) {
        if (currentTarget != null && !isAtTarget(owner.getTransform().getPosition())) {
            fireEvent(new ChangeEntityStateEvent(owner.getID(), EntityState.MOVING));
            float newAngle =
                (float) Math.atan2(currentTarget.x - owner.getTransform().getPosition().x, currentTarget.z
                    - owner.getTransform().getPosition().z);
            newAngle = (float) (newAngle * (180 / Math.PI));
            setTargetRotation(new Vector3f(0, newAngle, 0));
        } else if (currentTarget != null) {
            fireEvent(new ChangeEntityStateEvent(owner.getID(), EntityState.STANDING));
        }
    }

    private boolean isAtTarget(Vector3f position) {
        if (Vector3f.sub(position, currentTarget, null).lengthSquared() < DISTANCE_TO_TARGET) {
            fireEvent(new ChangeEntityStateEvent(owner.getID(), EntityState.STANDING));
            return true;
        }
        return false;
    }

    @Override
    public boolean isValidForSaving() {
        return true;
    }

    @Override
    public Map<String, Object> serialize(File savingLocation) {
        return new HashMap<String, Object>();
    }

    @Override
    public void deserialize(Map<String, Object> serialized, File loadingLocation) {

    }

    @Override
    public String serializeForNetwork() {
        if (currentTarget == null) {
            return "";
        } else {
            return "" + currentTarget.x + ";" + currentTarget.y + ";" + currentTarget.z + ";" + targetRotation.y;
        }
    }

    @Override
    public void deserializeFromNetwork(String serialized) {
        if (serialized != null && serialized.isEmpty()) {
            currentTarget = null;
        } else {
            if (currentTarget == null) {
                currentTarget = new Vector3f();
                targetRotation = new Vector3f();
            }
            String[] split = serialized.split(";");
            currentTarget.x = Float.valueOf(split[0]);
            currentTarget.y = Float.valueOf(split[1]);
            currentTarget.z = Float.valueOf(split[2]);
            targetRotation.y = Float.valueOf(split[3]);
        }
    }

    public Vector3f getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(Vector3f currentTarget) {
        this.currentTarget = currentTarget;
    }

    public Vector3f getTargetRotation() {
        return targetRotation;
    }

    public void setTargetRotation(Vector3f targetRotation) {
        this.targetRotation = targetRotation;
    }

}
