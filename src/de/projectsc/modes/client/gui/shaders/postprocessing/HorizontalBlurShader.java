package de.projectsc.modes.client.gui.shaders.postprocessing;

import de.projectsc.modes.client.gui.shaders.Shader;

/**
 * Postprocessing effect shader for gaussian blur.
 * 
 * @author Josch Bosch
 */
public class HorizontalBlurShader extends Shader {

    private static final String VERTEX_FILE = "postprocessing/gaussianBlur/hBlur.vert";

    private static final String FRAGMENT_FILE = "postprocessing/gaussianBlur/blur.frag";

    private int locationTargetWidth;

    public HorizontalBlurShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    /**
     * Load width to the shader.
     * 
     * @param width to load.
     */
    public void loadTargetWidth(float width) {
        super.loadFloat(locationTargetWidth, width);
    }

    @Override
    protected void getAllUniformLocations() {
        locationTargetWidth = super.getUniformLocation("targetWidth");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}
