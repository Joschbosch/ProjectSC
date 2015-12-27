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
public class AxisAlignedBoundingBox {

    private Vector3f min;

    private Vector3f max;

    private Vector3f center;

    private Vector3f size;

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
        center = Vector3f.add(min, max, null);
        center.scale(1.0f / 2.0f);
        setSize(Vector3f.sub(max, min, null));
        this.position = new Vector3f(0, 0, 0);
        this.scale = new Vector3f(1, 1, 1);
    }

    /**
     * Check if the current entity intersects with the picking ray.
     * 
     * @param org position of camera
     * @param ray to intersect
     * @return true if it intersects
     */
    public boolean intersects(Vector3f entityPosition, Vector3f ray, Vector3f org) {
        System.out.println("check  " + org + "  " + ray + " " + entityPosition + "   " + getMin() + "   " + getMax());
        Vector3f lb = Vector3f.add(entityPosition, getMin(), null);
        Vector3f rt = Vector3f.add(entityPosition, getMax(), null);
        // r.dir is unit direction vector of ray
        float dirfracx = 1.0f / ray.x;
        float dirfracy = 1.0f / ray.y;
        float dirfracz = 1.0f / ray.z;
        // lb is the corner of AABB with minimal coordinates - left bottom, rt is maximal corner
        // r.org is origin of ray
        float t1 = (lb.x - org.x) * dirfracx;
        float t2 = (rt.x - org.x) * dirfracx;
        float t3 = (lb.y - org.y) * dirfracy;
        float t4 = (rt.y - org.y) * dirfracy;
        float t5 = (lb.z - org.z) * dirfracz;
        float t6 = (rt.z - org.z) * dirfracz;

        float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        float t;
        // if tmax < 0, ray (line) is intersecting AABB, but whole AABB is behind us
        if (tmax < 0) {
            t = tmax;
            System.out.println("f1");
            return false;
        }

        // if tmin > tmax, ray doesn't intersect AABB
        if (tmin > tmax) {
            t = tmax;
            System.out.println("f2");
            return false;
        }

        t = tmin;
        return true;
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

}
