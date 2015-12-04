/*
 * Copyright (C) 2015
 */

package de.projectsc.core.modes.client.gui.objects;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**
 * One particle.
 * 
 * @author Josch Bosch
 */
public class Particle implements Comparable<Particle> {

    private Vector3f position;

    private float size;

    private Vector3f direction;

    private final Vector4f color;

    private float angle;

    private float weight;

    private float lifetime;

    private float cameradistance;

    private float startLifeTime;

    public Particle() {
        position = new Vector3f(0, 0, 0);
        color = new Vector4f(0, 0, 0, 0);
    }

    @Override
    public int compareTo(Particle arg0) {
        float dist = (this.cameradistance - arg0.getCameradistance());
        if (dist > 0 && dist <= 1) {
            return -1;
        } else if (dist < 0 && dist >= -1) {
            return 1;
        } else {
            return (int) -(this.cameradistance - arg0.getCameradistance());
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    /**
     * @param position new position
     */
    public void setPosition(Vector3f position) {
        if (this.position == null) {
            this.position = new Vector3f(0, 0, 0);
        }
        this.position.x = position.x;
        this.position.y = position.y;
        this.position.z = position.z;
    }

    public Vector3f getDirection() {
        return direction;
    }

    /**
     * @param speed to set.
     */
    public void setDirection(Vector3f speed) {
        if (this.direction != null) {
            this.direction.x = speed.x;
            this.direction.y = speed.y;
            this.direction.z = speed.z;
        } else {
            this.direction = speed;
        }
    }

    public Vector4f getColor() {
        return color;
    }

    /**
     * 
     * @param color new color
     */
    public void setColor(Vector4f color) {
        this.color.x = color.x;
        this.color.y = color.y;
        this.color.z = color.z;
        this.color.w = color.w;
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

    public float getStartLifeTime() {
        return startLifeTime;
    }

    public void setStartLifeTime(float startLifeTime) {
        this.startLifeTime = startLifeTime;
    }
}
