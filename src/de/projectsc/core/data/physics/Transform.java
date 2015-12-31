/*
 * Copyright (C) 2015
 */

package de.projectsc.core.data.physics;

import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.CoreConstants;

/**
 * The current location, rotation and scale of an entity.
 * 
 * @author Josch Bosch
 */
public class Transform {

    private static final String Z = "z";

    private static final String Y = "y";

    private static final String X = "x";

    private static final String SCALE = "scale";

    private static final String ROTATION = "rotation";

    private static final String POSITION = "position";

    private Vector3f position = new Vector3f(0, 0, 0);

    private Vector3f rotation = new Vector3f(0, 0, 0);

    private Vector3f scale = new Vector3f(1, 1, 1);

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "" + position.x + CoreConstants.SERIALIZATION_SEPARATOR + position.y + CoreConstants.SERIALIZATION_SEPARATOR + position.z
            + CoreConstants.SERIALIZATION_SEPARATOR + rotation.x + CoreConstants.SERIALIZATION_SEPARATOR + rotation.y
            + CoreConstants.SERIALIZATION_SEPARATOR + rotation.z + CoreConstants.SERIALIZATION_SEPARATOR
            + scale.x + CoreConstants.SERIALIZATION_SEPARATOR + scale.y + CoreConstants.SERIALIZATION_SEPARATOR + scale.z;
    }

    /**
     * Read transform values from a map.
     * 
     * @param values to read.
     */
    public void parseTransformValues(Map<String, Map<String, Double>> values) {
        this.position.x = (float) (double) values.get(POSITION).get(X);
        this.position.y = (float) (double) values.get(POSITION).get(Y);
        this.position.z = (float) (double) values.get(POSITION).get(Z);
        this.rotation.x = (float) (double) values.get(ROTATION).get(X);
        this.rotation.y = (float) (double) values.get(ROTATION).get(Y);
        this.rotation.z = (float) (double) values.get(ROTATION).get(Z);
        this.scale.x = (float) (double) values.get(SCALE).get(X);
        this.scale.y = (float) (double) values.get(SCALE).get(Y);
        this.scale.z = (float) (double) values.get(SCALE).get(Z);
    }
}
