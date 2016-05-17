package de.projectsc.modes.client.gui.postProcessing.gaussianBlur;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import de.projectsc.modes.client.gui.postProcessing.ImageRenderer;
import de.projectsc.modes.client.gui.shaders.postprocessing.VerticalBlurShader;

public class VerticalBlur {

    private ImageRenderer renderer;

    private VerticalBlurShader shader;

    public VerticalBlur(int targetFboWidth, int targetFboHeight) {
        shader = new VerticalBlurShader();
        renderer = new ImageRenderer(targetFboWidth, targetFboHeight);
        shader.start();
        shader.loadTargetHeight(targetFboHeight);
        shader.stop();
    }

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

    public void dispose() {
        renderer.dispose();
        shader.dispose();
    }
}
