/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import de.projectsc.gui.entities.Entity;
import de.projectsc.gui.models.RawModel;
import de.projectsc.gui.models.TexturedModel;
import de.projectsc.gui.shaders.Shader;
import de.projectsc.gui.shaders.StaticShader;
import de.projectsc.gui.tools.Maths;

/**
 * This class will get {@link Entity} objects to render onto the screen.
 * 
 * @author Josch Bosch
 */
public final class Renderer {

    private Renderer() {

    }

    /**
     * Renders the given entity using the given shader to the current screen.
     * 
     * @param entity to render
     * @param shader used for the rendering
     */
    public static void renderModel(Entity entity, Shader shader) {
        TexturedModel tModel = entity.getModel();
        RawModel model = tModel.getRawModel();
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        Matrix4f transformationMatrix =
            Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());

        ((StaticShader) shader).loadTransformationMatrix(transformationMatrix);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tModel.getTexture().getTextureID());
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

}
