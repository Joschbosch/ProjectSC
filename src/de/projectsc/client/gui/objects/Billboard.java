/*
 * Copyright (C) 2015
 */

package de.projectsc.client.gui.objects;

import java.io.File;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.tools.Loader;

/**
 * Billboards for the units (e.g. healthbar).
 * 
 * @author Josch Bosch
 */
public class Billboard {

    private Vector3f position;

    private Vector2f size;

    private File imageFile;

    private final Loader loader;

    private int textureID;

    public Billboard(Loader loader) {
        this.loader = loader;

    }

    public int getTexture() {
        return textureID;
    }

    /**
     * @param position to set
     */
    public void setPosition(Vector3f position) {
        if (this.position != null) {
            this.position.x = position.x;
            this.position.y = position.y;
            this.position.z = position.z;
        } else {
            this.position = position;
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector2f getSize() {
        return size;
    }

    public void setSize(Vector2f size) {
        this.size = size;
    }

    public File getImageFile() {
        return imageFile;
    }

    /**
     * @param imageFile image for the board
     */
    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
        textureID = loader.loadTexture(imageFile);
    }

}
