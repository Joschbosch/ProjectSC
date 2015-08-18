/*
 * Copyright (C) 2015
 */

package de.projectsc.client.gui.objects;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Particle implements Comparable<Particle> {

    private Vector3f position;

    private Vector3f speed;

    private Vector4f color;

    private float size;

    private float angle;

    private float weight;

    private float lifetime;

    private float cameradistance;

    @Override
    public int compareTo(Particle arg0) {
        float dist = (this.cameradistance - arg0.getCameradistance());
        if (dist > 0 && dist <= 1) {
            return 1;
        } else if (dist < 0 && dist >= -1) {
            return -1;
        } else {
            return (int) (this.cameradistance - arg0.getCameradistance());
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getSpeed() {
        return speed;
    }

    public void setSpeed(Vector3f speed) {
        if (this.speed != null) {
            this.speed.x = speed.x;
            this.speed.y = speed.y;
            this.speed.z = speed.z;
        } else {
            this.speed = speed;
        }
    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getLifetime() {
        return lifetime;
    }

    public void setLifetime(float lifetime) {
        this.lifetime = lifetime;
    }

    public float getCameradistance() {
        return cameradistance;
    }

    public void setCameradistance(float cameradistance) {
        this.cameradistance = cameradistance;
    }

}
