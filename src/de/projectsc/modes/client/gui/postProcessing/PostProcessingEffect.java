/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.client.gui.postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import de.projectsc.modes.client.gui.shaders.Shader;

/**
 * Base class for all post processing effects.
 * 
 * @author Josch Bosch
 */
public abstract class PostProcessingEffect {

    protected Shader shader;

    private ImageRenderer renderer;

    public PostProcessingEffect(int targetFboWidth, int targetFboHeight) {
        renderer = new ImageRenderer(targetFboWidth, targetFboHeight);
        addShader(targetFboWidth, targetFboHeight);
    }

    protected abstract void addShader(int targetFboWidth, int targetFboHeight);

    /**
     * Render the given texture.
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
     * Clean up the mess.
     */
    public void dispose() {
        renderer.dispose();
        shader.dispose();
    }
}
