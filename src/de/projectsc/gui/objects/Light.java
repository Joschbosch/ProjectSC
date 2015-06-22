/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.objects;

import org.lwjgl.util.vector.Vector3f;

/**
 * All light information for rendering.
 * 
 * @author Josch Bosch
 */
public class Light {

    private Vector3f position;

    private Vector3f color;

    private Vector3f attenuation = new Vector3f(1, 0, 0);

    public Light(Vector3f position, Vector3f color) {
        super();
        this.position = position;
        this.color = color;
    }

    public Light(Vector3f position, Vector3f color, Vector3f attenuation) {
        super();
        this.position = position;
        this.color = color;
        this.setAttenuation(attenuation);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(Vector3f attenuation) {
        this.attenuation = attenuation;
    }

}
