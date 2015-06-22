/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.gui.shaders;

import org.lwjgl.util.vector.Matrix4f;

public class UIShader extends Shader {

    private static final String VERTEX_FILE = "uiShader.vert";

    private static final String FRAGMENT_FILE = "uiShader.frag";

    private int location_transformationMatrix;

    public UIShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadTransformation(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}
