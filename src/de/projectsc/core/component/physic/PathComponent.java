/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.component.physic;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.CoreConstants;
import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.interfaces.Component;

/**
 * Component for the path of an entity.
 * 
 * @author Josch Bosch
 */
public class PathComponent extends DefaultComponent {

    /**
     * ID.
     */
    public static final String NAME = "Path Component";

    private Vector3f currentTarget = null;

    public PathComponent() {
        setComponentName(NAME);
        setType(ComponentType.PHYSICS);
    }

    @Override
    public String serializeForNetwork() {
        if (currentTarget == null) {
            return "";
        } else {
            return "" + currentTarget.x + CoreConstants.SERIALIZATION_SEPARATOR + currentTarget.y + CoreConstants.SERIALIZATION_SEPARATOR
                + currentTarget.z;
        }
    }

    @Override
    public void deserializeFromNetwork(String serialized) {
        if (serialized != null && serialized.isEmpty()) {
            currentTarget = null;
        } else {
            if (currentTarget == null) {
                currentTarget = new Vector3f();
            }
            String[] split = serialized.split(CoreConstants.SERIALIZATION_SEPARATOR);
            currentTarget.x = Float.valueOf(split[0]);
            currentTarget.y = Float.valueOf(split[1]);
            currentTarget.z = Float.valueOf(split[2]);
        }
    }

    @Override
    public Component cloneComponent() {
        PathComponent pc = new PathComponent();
        return pc;
    }

    public Vector3f getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(Vector3f currentTarget) {
        this.currentTarget = currentTarget;
    }

}
