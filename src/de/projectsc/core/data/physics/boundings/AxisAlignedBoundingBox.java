/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.data.physics.boundings;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.physics.BoundingVolume;
import de.projectsc.core.data.physics.BoundingVolumeType;
import de.projectsc.core.utils.Maths;

/**
 * An axis aligned bounding box for each entitiy.
 * 
 * @author Josch Bosch
 */
public class AxisAlignedBoundingBox implements BoundingVolume {

    private Vector3f min;

    private Vector3f max;

    private Vector3f position;

    private Vector3f scale;

    /**
     * Minimum and maximum value for all axis.
     * 
     * @param min vector
     * @param max vector
     */
    public AxisAlignedBoundingBox(Vector3f min, Vector3f max) {
        super();
        this.min = min;
        this.max = max;
        Vector3f center = Maths.getCenter(this);
        Vector3f size = Maths.getSize(this);
        this.position = new Vector3f(center.x, center.y - size.y/2, center.z);
        this.scale = new Vector3f(1, 1, 1);
    }

    @Override
    public String toString() {
        String result = String.format("\nBounding box:\n\tMinium (%s)\n\tMaximum %s\n\tPosition %s\n", min, max, position);
        return result;

    }

    /**
     * @param newPosition to set
     */
    @Override
    public void setPositionOffset(Vector3f newPosition) {
        this.position = newPosition;
    }

    /**
     * Sets new scale and recalculates center.
     * 
     * @param scale to set
     */
    @Override
    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    @Override
    public Vector3f getPositionOffset() {
        return position;
    }

    @Override
    public Vector3f getMinima() {
        return min;
    }

    @Override
    public Vector3f getMaxima() {
        return max;
    }

    @Override
    public Vector3f getScale() {
        return scale;
    }

    @Override
    public BoundingVolumeType getType() {
        return BoundingVolumeType.AXIS_ALIGNED_BOUNDING_BOX;
    }

    @Override
    public BoundingVolume cloneVolume() {
        AxisAlignedBoundingBox aabb = new AxisAlignedBoundingBox(new Vector3f(min), new Vector3f(max));
        aabb.setScale(new Vector3f(scale));
        aabb.setPositionOffset(new Vector3f(position));
        return aabb;
    }

    public void setMaxima(Vector3f newMax) {
        this.max = newMax;
    }
    public void setMinima(Vector3f newMin) {
        this.min = newMin;
    }
}
