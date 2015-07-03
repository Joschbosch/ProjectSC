/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.gui.ui;

import org.lwjgl.util.vector.Vector2f;

/**
 * Texture class for all UI elements.
 * 
 * @author Josch Bosch
 */
public class UITexture {

    private final int texture;

    private final Vector2f position;

    private final Vector2f scale;

    public UITexture(int texture, Vector2f position, Vector2f scale) {
        super();
        this.texture = texture;
        this.position = position;
        this.scale = scale;
    }

    public int getTexture() {
        return texture;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getScale() {
        return scale;
    }

}