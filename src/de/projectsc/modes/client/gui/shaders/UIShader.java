/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.client.gui.shaders;

import org.lwjgl.util.vector.Matrix4f;

/**
 * Shader for all UI elements.
 * 
 * @author Josch Bosch
 */
public class UIShader extends Shader {

    private static final String VERTEX_FILE = "uiShader.vert";

    private static final String FRAGMENT_FILE = "uiShader.frag";

    private int locationTransformationMatrix;

    public UIShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    /**
     * Loads the transformation matrix to the shader.
     * 
     * @param matrix transformation matrix
     */
    public void loadTransformation(Matrix4f matrix) {
        super.loadMatrix(locationTransformationMatrix, matrix);
    }

    @Override
    protected void getAllUniformLocations() {
        locationTransformationMatrix = super.getUniformLocation("transformationMatrix");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}
