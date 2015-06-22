/*
 * Copyright (C) 2015 
 */

package de.projectsc.gui.render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import de.projectsc.gui.models.RawModel;
import de.projectsc.gui.shaders.UIShader;
import de.projectsc.gui.tools.Loader;
import de.projectsc.gui.tools.Maths;
import de.projectsc.gui.ui.UITexture;

public class UIRenderer {

    private final RawModel quad;

    private UIShader shader;

    public UIRenderer(Loader loader) {
        float[] positions = { 0 - 1, 1, 0 - 1, -1, 1, 1, 1, 0 - 1 };
        quad = loader.loadToVAO(positions);
        shader = new UIShader();
    }

    public void render(List<UITexture> uis) {
        shader.start();
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        for (UITexture ui : uis) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ui.getTexture());
            Matrix4f matrix = Maths.createTransformationMatrix(ui.getPosition(), ui.getScale());
            shader.loadTransformation(matrix);
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        }

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        shader.stop();
    }

    public void dispose() {
        shader.dispose();
    }
}
