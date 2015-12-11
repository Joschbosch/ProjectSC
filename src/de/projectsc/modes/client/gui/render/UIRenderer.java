/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui.render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import de.projectsc.core.utils.Maths;
import de.projectsc.modes.client.gui.models.RawModel;
import de.projectsc.modes.client.gui.shaders.UIShader;
import de.projectsc.modes.client.gui.textures.UITexture;
import de.projectsc.modes.client.gui.utils.Loader;

/**
 * Renderer class for all UI elements.
 * 
 * @author Josch Bosch
 */
public class UIRenderer {

    private final RawModel quad;

    private final UIShader shader;

    public UIRenderer() {
        float[] positions = { 0 - 1, 1, 0 - 1, 0 - 1, 1, 1, 1, 0 - 1 };
        quad = Loader.loadToVAO(positions, 2);
        shader = new UIShader();
    }

    /**
     * Render all ui elements.
     * 
     * @param uiElements to render
     */
    public void render(List<UITexture> uiElements) {
        shader.start();
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        // GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

        for (UITexture ui : uiElements) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ui.getTexture());
            Matrix4f matrix = Maths.createTransformationMatrix(ui.getPosition(), ui.getScale());
            shader.loadTransformation(matrix);
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        }
        // GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        shader.stop();
    }

    /**
     * Dispose everything.
     */
    public void dispose() {
        shader.dispose();
    }
}
