/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.shaders;

/**
 * A shader program that does not much.
 * 
 * @author Josch Bosch
 */
public class StaticShader extends Shader {

    private static final String VERTEX_FILE = "tutShader.vert";

    private static final String FRAGMENT_FILE = "tutShader.frag";

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");

    }

}
