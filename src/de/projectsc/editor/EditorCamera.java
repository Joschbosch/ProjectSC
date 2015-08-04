/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.editor;

import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.objects.GraphicalEntity;

public class EditorCamera extends Camera {

    private boolean rotateCamera;

    public EditorCamera(GraphicalEntity player) {
        super(player);
    }

    @Override
    public void move(float delta) {
        // super.move(delta);
        if (rotateCamera) {

        }
    }

    public boolean isRotateCamera() {
        return rotateCamera;
    }

    public void setRotateCamera(boolean rotateCamera) {
        this.rotateCamera = rotateCamera;
    }
}
