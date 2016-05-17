package de.projectsc.modes.client.gui.shaders.postprocessing;

import de.projectsc.modes.client.gui.shaders.Shader;

public class HorizontalBlurShader extends Shader {

    private static final String VERTEX_FILE = "postprocessing/gaussianBlur/hBlur.vert";

    private static final String FRAGMENT_FILE = "postprocessing/gaussianBlur/blur.frag";

    private int location_targetWidth;

    public HorizontalBlurShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadTargetWidth(float width) {
        super.loadFloat(location_targetWidth, width);
    }

    @Override
    protected void getAllUniformLocations() {
        location_targetWidth = super.getUniformLocation("targetWidth");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}
