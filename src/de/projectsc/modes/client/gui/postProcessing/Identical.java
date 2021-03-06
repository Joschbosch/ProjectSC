/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.client.gui.postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import de.projectsc.modes.client.gui.shaders.postprocessing.IdenticalShader;

/**
 * REsolves the post processing chain to the screen.
 * 
 * @author Josch Bosch
 */
public class Identical {

    private ImageRenderer renderer;

    private IdenticalShader shader;

    public Identical() {
        shader = new IdenticalShader();
        renderer = new ImageRenderer();
    }

    /**
     * Render given texture onto the screen.
     * 
     * @param texture to render.
     */
    public void render(int texture) {
        shader.start();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        renderer.renderQuad();
        shader.stop();
    }

    public int getOutputTexture() {
        return renderer.getOutputTexture();
    }

    /**
     * Dispose everything.
     */
    public void dispose() {
        renderer.dispose();
        shader.dispose();
    }
}
