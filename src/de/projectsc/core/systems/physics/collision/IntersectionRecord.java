/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.systems.physics.collision;

import org.lwjgl.util.vector.Vector3f;

/**
 * Records an intersection in the collision system.
 * 
 * @author Josch Bosch
 */
public class IntersectionRecord {

    private Vector3f position;

    private Vector3f normal;

    private Vector3f ray;

    private String object1;

    private String object2;

    private OctreeNode node;

    private boolean hasHit = false;

    private double distance;

    public IntersectionRecord() {
        position = new Vector3f();
        normal = new Vector3f();
        ray = new Vector3f();
        distance = Double.MAX_VALUE;
        object1 = null;
    }

    public IntersectionRecord(Vector3f hitPos, Vector3f hitNormal, Vector3f ray, double distance) {
        position = hitPos;
        normal = hitNormal;
        this.ray = ray;
        this.distance = distance;
        hasHit = true;
    }

    @Override
    public boolean equals(Object obj) {
        IntersectionRecord other = (IntersectionRecord) obj;
        if (obj == null) {
            return false;
        } else if (other.getObject1().equals(object1) && other.getObject2().equals(object2)) {
            return true;
        } else if (other.getObject1().equals(object2) && other.getObject2().equals(object1)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public void setNormal(Vector3f normal) {
        this.normal = normal;
    }

    public Vector3f getRay() {
        return ray;
    }

    public void setRay(Vector3f ray) {
        this.ray = ray;
    }

    public String getObject1() {
        return object1;
    }

    public void setObject1(String object1) {
        this.object1 = object1;
    }

    public String getObject2() {
        return object2;
    }

    public void setObject2(String object2) {
        this.object2 = object2;
    }

    public OctreeNode getNode() {
        return node;
    }

    public void setNode(OctreeNode node) {
        this.node = node;
    }

    public boolean isHasHit() {
        return hasHit;
    }

    public void setHasHit(boolean hasHit) {
        this.hasHit = hasHit;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
