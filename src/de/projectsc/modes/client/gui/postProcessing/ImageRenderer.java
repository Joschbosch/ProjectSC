package de.projectsc.modes.client.gui.postProcessing;

import org.lwjgl.opengl.GL11;

/**
 * Renders the given fbo onto a quad.
 * 
 * @author Josch Boschj
 */
public class ImageRenderer {

    private FrameBufferObject fbo;

    public ImageRenderer(int width, int height) {
        this.fbo = new FrameBufferObject(width, height, FrameBufferObject.NONE);
    }

    public ImageRenderer() {}

    /**
     * Render the buffer.
     */
    public void renderQuad() {
        if (fbo != null) {
            fbo.bindFrameBuffer();
        }
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
        if (fbo != null) {
            fbo.unbindFrameBuffer();
        }
    }

    public int getOutputTexture() {
        return fbo.getColourTexture();
    }

    /**
     * Dispose everything.
     */
    public void dispose() {
        if (fbo != null) {
            fbo.dispose();
        }
    }

}
