package de.projectsc.modes.client.gui.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

/**
 * Shader for particles.
 * 
 * @author Josch Bosch
 */
public class ParticleShader extends Shader {

    private static final String VERTEX_FILE = "particleShader.vert";

    private static final String FRAGMENT_FILE = "particleShader.frag";

    private int locationModelViewMatrix;

    private int locationProjectionMatrix;

    private int locationTexOffset1;

    private int locationTexOffset2;

    private int locationTexCoordInfo;

    public ParticleShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        locationModelViewMatrix = super.getUniformLocation("modelViewMatrix");
        locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
        locationTexOffset1 = super.getUniformLocation("texOffset1");
        locationTexOffset2 = super.getUniformLocation("texOffset2");
        locationTexCoordInfo = super.getUniformLocation("texCoordInfo");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    /**
     * Load texture coords.
     * 
     * @param offset1 current texture
     * @param offset2 next texture
     * @param numberOfRows of texture
     * @param blendFactor between textures
     */
    public void loadTextureCoordinates(Vector2f offset1, Vector2f offset2, float numberOfRows, float blendFactor) {
        super.loadVector(locationTexOffset1, offset1);
        super.loadVector(locationTexOffset2, offset2);
        super.loadVector(locationTexCoordInfo, new Vector2f(numberOfRows, blendFactor));
    }

    /**
     * @param modelView to load
     */
    public void loadModelViewMatrix(Matrix4f modelView) {
        super.loadMatrix(locationModelViewMatrix, modelView);
    }

    /**
     * @param projectionMatrix to load
     */
    public void loadProjectionMatrix(Matrix4f projectionMatrix) {
        super.loadMatrix(locationProjectionMatrix, projectionMatrix);
    }

}
