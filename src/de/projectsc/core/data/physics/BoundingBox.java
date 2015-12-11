/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.data.physics;

import org.lwjgl.util.vector.Vector3f;

/**
 * An axis aligned bounding box for each entitiy.
 * 
 * @author Josch Bosch
 */
public class BoundingBox {

    private Vector3f min;

    private Vector3f max;

    private Vector3f center;

    private Vector3f size;

    private Vector3f position;

    private Vector3f rotation;

    private Vector3f scale;

    /**
     * Minimum and maximum value for all axis.
     * 
     * @param min vector
     * @param max vector
     */
    public BoundingBox(Vector3f min, Vector3f max) {
        super();
        this.min = min;
        this.max = max;
        center = Vector3f.add(min, max, null);
        center.scale(1.0f / 2.0f);
        setSize(Vector3f.sub(max, min, null));
    }

    public Vector3f getMin() {
        return min;
    }

    public void setMin(Vector3f min) {
        this.min = min;
    }

    public Vector3f getMax() {
        return max;
    }

    public void setMax(Vector3f max) {
        this.max = max;
    }

    public Vector3f getSize() {
        return size;
    }

    public void setSize(Vector3f size) {
        this.size = size;
    }

    public Vector3f getCenter() {
        return center;
    }

    public Vector3f getCenterWithPosition() {
        return Vector3f.add(center, position, null);
    }

    public void setCenter(Vector3f center) {
        this.center = center;
    }

    @Override
    public String toString() {
        String result = String.format("Bounding box:\n\tMinium (%s)\n\tMaximum (%s)\n\tCenter (%s)\n\tSize (%s)", min, max, center, size);
        return result;

    }

    public Vector3f getPosition() {
        return position;
    }

    /**
     * @param position to set
     */
    public void setPosition(Vector3f position) {
        if (this.position == null) {
            this.position = position;
        } else {
            this.position.x = position.x;
            this.position.y = position.y;
            this.position.z = position.z;
        }
    }

    public Vector3f getScale() {
        return scale;
    }

    /**
     * Sets new scale and recalculates center.
     * 
     * @param scale to set
     */
    public void setScale(Vector3f scale) {
        this.scale = scale;
        min.x = min.x * scale.x;
        min.y = min.y * scale.y;
        min.z = min.z * scale.z;
        max.x = max.x * scale.x;
        max.y = max.y * scale.y;
        max.z = max.z * scale.z;
        center = Vector3f.add(min, max, null);
        center.scale(1.0f / 2.0f);
    }

    public Vector3f getRotation() {
        return rotation;
    }

    /**
     * @param rotation to set
     */
    public void setRotation(Vector3f rotation) {
        if (this.rotation == null) {
            this.rotation = rotation;
        } else {
            this.rotation.x = rotation.x;
            this.rotation.y = rotation.y;
            this.rotation.z = rotation.z;
        }
    }
}
