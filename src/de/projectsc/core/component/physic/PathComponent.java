/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.component.physic;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.CoreConstants;
import de.projectsc.core.component.ComponentType;

/**
 * Component for the path of an entity.
 * 
 * @author Josch Bosch
 */
public class PathComponent extends PhysicsComponent {

    /**
     * ID.
     */
    public static final String NAME = "Path Component";

    private Vector3f currentTarget = null;

    private Vector3f targetRotation;

    public PathComponent() {
        setComponentName(NAME);
        setType(ComponentType.PHYSICS);
    }

    @Override
    public void update(long elapsed) {

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
    public String serializeForNetwork() {
        if (currentTarget == null) {
            return "";
        } else {
            return "" + currentTarget.x + CoreConstants.SERIALIZATION_SEPARATOR + currentTarget.y + CoreConstants.SERIALIZATION_SEPARATOR
                + currentTarget.z + CoreConstants.SERIALIZATION_SEPARATOR + targetRotation.y;
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
            String[] split = serialized.split(CoreConstants.SERIALIZATION_SEPARATOR);
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
