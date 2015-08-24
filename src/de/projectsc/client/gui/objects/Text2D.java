/*
 * Copyright (C) 2015 
 */

package de.projectsc.client.gui.objects;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.tools.Loader;

public class Text2D {

    private String text;

    private Vector3f positionAndSize;

    private int textureId;

    public Text2D(String text, float x, float y, float size, String style) {
        this.text = text;
        this.positionAndSize = new Vector3f(x, y, size);
        textureId = Loader.loadTexture(style);
    }

    public int getTextStyle() {
        return textureId;
    }

    public int getLength() {
        return text.length();
    }

    public float getSize() {
        return positionAndSize.z;
    }

    public float getX() {
        return positionAndSize.x;
    }

    public float getY() {
        return positionAndSize.y;
    }

    public String getText() {
        return text;
    }

}
