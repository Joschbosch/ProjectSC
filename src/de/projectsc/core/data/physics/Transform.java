/*
 * Copyright (C) 2015
 */

package de.projectsc.core.data.physics;

import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

/**
 * The current location, rotation and scale of an entity.
 * 
 * @author Josch Bosch
 */
public class Transform {

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
        return "" + position.x + ";" + position.y + ";" + position.z + ";" + rotation.x + ";" + rotation.y + ";" + rotation.z + ";"
            + scale.x + ";" + scale.y + ";" + scale.z;
    }

    public void parseTransformValues(Map<String, Map<String, Double>> values) {
        this.position.x = (float) (double) values.get("position").get("x");
        this.position.y = (float) (double) values.get("position").get("y");
        this.position.z = (float) (double) values.get("position").get("z");
        this.rotation.x = (float) (double) values.get("rotation").get("x");
        this.rotation.y = (float) (double) values.get("rotation").get("y");
        this.rotation.z = (float) (double) values.get("rotation").get("z");
        this.scale.x = (float) (double) values.get("scale").get("x");
        this.scale.y = (float) (double) values.get("scale").get("y");
        this.scale.z = (float) (double) values.get("scale").get("z");
    }
}
