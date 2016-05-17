package de.projectsc.modes.client.gui.shaders.postprocessing;

import de.projectsc.modes.client.gui.shaders.Shader;

public class ContrastShader extends Shader {

    private static final String VERTEX_FILE = "/postprocessing/ppContrast.vert";

    private static final String FRAGMENT_FILE = "/postprocessing/ppContrast.frag";

    public ContrastShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {}

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}
