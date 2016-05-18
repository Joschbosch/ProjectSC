package de.projectsc.modes.client.gui.shaders.postprocessing;

import de.projectsc.modes.client.gui.shaders.Shader;

/**
 * Shader for gaussian blur effect.
 * 
 * @author Josch Bosch
 */
public class VerticalBlurShader extends Shader {

    private static final String VERTEX_FILE = "postprocessing/gaussianBlur/vBlur.vert";

    private static final String FRAGMENT_FILE = "postprocessing/gaussianBlur/blur.frag";

    private int locationTargetHeight;

    public VerticalBlurShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    /**
     * Load height into shader.
     * 
     * @param height to load
     */
    public void loadTargetHeight(float height) {
        super.loadFloat(locationTargetHeight, height);
    }

    @Override
    protected void getAllUniformLocations() {
        locationTargetHeight = super.getUniformLocation("targetHeight");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}
