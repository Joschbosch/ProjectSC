/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.gui.objects;

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

    private String name;

    public Light(Vector3f position, Vector3f color, String name) {
        super();
        this.position = position;
        this.color = color;
        this.setName(name);
    }

    public Light(Vector3f position, Vector3f color, Vector3f attenuation, String name) {
        super();
        this.position = position;
        this.color = color;
        this.setAttenuation(attenuation);
        this.name = name;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position.x = position.x;
        this.position.y = position.y;
        this.position.z = position.z;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
