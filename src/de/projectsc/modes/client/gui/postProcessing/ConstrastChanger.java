/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.client.gui.postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import de.projectsc.modes.client.gui.shaders.postprocessing.ContrastShader;

public class ConstrastChanger {

    private ImageRenderer renderer;

    private ContrastShader shader;

    public ConstrastChanger() {
        shader = new ContrastShader();
        renderer = new ImageRenderer();
    }

    public void render(int texture) {
        shader.start();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        renderer.renderQuad();
        shader.stop();
    }

    public void dispose() {
        renderer.dispose();
        shader.dispose();
    }

}
