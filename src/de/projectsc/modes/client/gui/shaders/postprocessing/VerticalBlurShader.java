package de.projectsc.modes.client.gui.shaders.postprocessing;

import de.projectsc.modes.client.gui.shaders.Shader;

public class VerticalBlurShader extends Shader {

    private static final String VERTEX_FILE = "postprocessing/gaussianBlur/vBlur.vert";

    private static final String FRAGMENT_FILE = "postprocessing/gaussianBlur/blur.frag";

    private int location_targetHeight;

    public VerticalBlurShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadTargetHeight(float height) {
        super.loadFloat(location_targetHeight, height);
    }

    @Override
    protected void getAllUniformLocations() {
        location_targetHeight = super.getUniformLocation("targetHeight");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}
