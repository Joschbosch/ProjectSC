/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.shaders;

/**
 * Shader for correct camera movement.
 * 
 * @author Josch Bosch
 */
public class UniformColorShader extends Shader {

    private static final String VERTEX_FILE = "tilemap.vert";

    private static final String FRAGMENT_FILE = "tilemap.frag";

    public UniformColorShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);

    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "modelToWorldMatrix");
        bindAttribute(1, "worldToCameraMatrix");
        bindAttribute(2, "cameraToClipMatrix");
        bindAttribute(3, "baseColor");
    }

}
