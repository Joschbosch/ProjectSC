package de.projectsc.modes.client.gui.shaders;

import org.lwjgl.util.vector.Matrix4f;

/**
 * Shader for particles.
 * 
 * @author Josch Bosch
 */
public class ParticleShader extends Shader {

    private static final String VERTEX_FILE = "particleShader.vert";

    private static final String FRAGMENT_FILE = "particleShader.frag";

    private int locationProjectionMatrix;

    private int locationNumberOfRows;

    public ParticleShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
        locationNumberOfRows = super.getUniformLocation("number OfRows");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "modelViewMatrix");
        super.bindAttribute(5, "texOffset");
        super.bindAttribute(6, "blendFactor");

    }

    /**
     * @param numberOfRows to load
     */
    public void loadNumberOfRows(float numberOfRows) {
        super.loadFloat(locationNumberOfRows, numberOfRows);
    }

    /**
     * @param projectionMatrix to load
     */
    public void loadProjectionMatrix(Matrix4f projectionMatrix) {
        super.loadMatrix(locationProjectionMatrix, projectionMatrix);
    }

}
