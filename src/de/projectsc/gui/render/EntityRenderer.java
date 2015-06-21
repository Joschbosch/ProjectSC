/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.render;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import de.projectsc.gui.entities.Entity;
import de.projectsc.gui.models.RawModel;
import de.projectsc.gui.models.TexturedModel;
import de.projectsc.gui.shaders.EntityShader;
import de.projectsc.gui.textures.ModelTexture;
import de.projectsc.gui.tools.Maths;

/**
 * This class will get {@link Entity} objects to render onto the screen.
 * 
 * @author Josch Bosch
 */
public class EntityRenderer {

    private EntityShader shader;

    public EntityRenderer(EntityShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    /**
     * Renders all textured models without switching vaos to often.
     * 
     * @param entities to render.
     */
    public void render(Map<TexturedModel, List<Entity>> entities) {
        for (TexturedModel model : entities.keySet()) {
            prepareTexturedModel(model);
            List<Entity> batch = entities.get(model);
            for (Entity e : batch) {
                prepareInstance(e);
                GL11.glDrawElements(GL11.GL_TRIANGLES, e.getModel().getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbindTexturedModel();
        }
    }

    private void prepareTexturedModel(TexturedModel tModel) {
        RawModel model = tModel.getRawModel();
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        ModelTexture texture = tModel.getTexture();
        if (texture.hasTransparency()) {
            MasterRenderer.disableCulling();
        }
        shader.loadShineValues(texture.getShineDamper(), texture.getReflectivity());
        shader.loadUseFakeLighting(texture.isFakeLighting());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
    }

    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
        MasterRenderer.enableCulling();
    }

    private void prepareInstance(Entity entity) {
        Matrix4f transformationMatrix =
            Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
