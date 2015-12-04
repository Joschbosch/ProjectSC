/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.modes.client.gui.render;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import de.projectsc.core.entities.Entity;
import de.projectsc.core.modes.client.gui.components.graphical.impl.ModelAndTextureComponent;
import de.projectsc.core.modes.client.gui.models.RawModel;
import de.projectsc.core.modes.client.gui.models.TexturedModel;
import de.projectsc.core.modes.client.gui.shaders.EntityShader;
import de.projectsc.core.modes.client.gui.textures.ModelTexture;
import de.projectsc.core.modes.client.gui.tools.Maths;

/**
 * This class will get {@link GraphicalEntity} objects to render onto the screen.
 * 
 * @author Josch Bosch
 */
public class EntityRenderer {

    private final EntityShader shader;

    public EntityRenderer(EntityShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    /**
     * Renders all textured models without switching vaos to often.
     * 
     * @param entitiesWithModel to render.
     */
    public void render(Map<TexturedModel, List<Entity>> entitiesWithModel) {
        for (TexturedModel model : entitiesWithModel.keySet()) {
            prepareTexturedModel(model);
            List<Entity> batch = entitiesWithModel.get(model);
            for (Entity e : batch) {
                ModelAndTextureComponent modelComponent = e.getComponent(ModelAndTextureComponent.class);
                prepareInstance(e, modelComponent);
                GL11.glDrawElements(GL11.GL_TRIANGLES, modelComponent.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
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
        if (texture.isTransparent()) {
            MasterRenderer.disableCulling();
        }
        shader.loadNumberRows(texture.getNumberOfRows());
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

    private void prepareInstance(Entity entity, ModelAndTextureComponent modelComponent) {
        Matrix4f transformationMatrix =
            Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(modelComponent.getTextureOffsetX(), modelComponent.getTextureOffsetY());
        shader.loadSelected(entity.isHighlighted(), entity.isSelected());

    }
}
