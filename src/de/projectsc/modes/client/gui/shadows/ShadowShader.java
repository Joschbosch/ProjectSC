package de.projectsc.modes.client.gui.shadows;

import org.lwjgl.util.vector.Matrix4f;

import de.projectsc.modes.client.gui.shaders.Shader;

/**
 * 
 * Shader for shadows.
 * 
 * @author Josch Bosch
 */
public class ShadowShader extends Shader {

    private static final String VERTEX_FILE = "shadowShader.vert";

    private static final String FRAGMENT_FILE = "shadowShader.frag";

    private int locationMVPMatrix;

    protected ShadowShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        locationMVPMatrix = super.getUniformLocation("mvpMatrix");

    }

    protected void loadMvpMatrix(Matrix4f mvpMatrix) {
        super.loadMatrix(locationMVPMatrix, mvpMatrix);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "in_position");
    }

}
