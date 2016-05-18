/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.client.gui.shaders.postprocessing;

import de.projectsc.modes.client.gui.shaders.Shader;

/**
 * Shader for identical effect.
 * 
 * @author Josch Bosch
 */
public class IdenticalShader extends Shader {

    private static final String VERTEX_FILE = "postprocessing/ident.vert";

    private static final String FRAGMENT_FILE = "postprocessing/ident.frag";

    public IdenticalShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {}
}
