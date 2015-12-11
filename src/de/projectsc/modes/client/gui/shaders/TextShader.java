/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.client.gui.shaders;

/**
 * Shader for 2d text.
 * 
 * @author Josch Bosch
 */
public class TextShader extends Shader {

    private static final String VERTEX_FILE = "textShader.vert";

    private static final String FRAGMENT_FILE = "textShader.frag";

    private int locationTexture;

    public TextShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    /**
     * Load up texture for text.
     * 
     * @param textureID to load
     */
    public void loadTexture(int textureID) {
        super.loadInt(locationTexture, textureID);
    }

    @Override
    protected void getAllUniformLocations() {
        locationTexture = super.getUniformLocation("texture");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "quadposition");
        super.bindAttribute(1, "position");
        super.bindAttribute(2, "vertexUV");
    }

}
