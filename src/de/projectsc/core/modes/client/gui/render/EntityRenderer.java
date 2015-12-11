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
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.modes.client.gui.models.RawModel;
import de.projectsc.core.modes.client.gui.models.TexturedModel;
import de.projectsc.core.modes.client.gui.shaders.EntityShader;
import de.projectsc.core.modes.client.gui.textures.ModelTexture;
import de.projectsc.core.utils.Maths;

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
    public void render(Map<TexturedModel, List<Long>> entitiesWithModel,
        Map<Long, Vector3f> position, Map<Long, Vector3f> rotations, Map<Long, Vector3f> scales) {
        for (TexturedModel model : entitiesWithModel.keySet()) {
            prepareTexturedModel(model);
            List<Long> batch = entitiesWithModel.get(model);
            for (Long e : batch) {
                if (position.get(e) != null && rotations.get(e) != null && scales.get(e) != null) {
                    prepareInstance(model.getTexture(), position.get(e), rotations.get(e), scales.get(e));
                    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
                }
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

    private void prepareInstance(ModelTexture modelTexture, Vector3f position, Vector3f rotation, Vector3f scale) {
        Matrix4f transformationMatrix =
            Maths.createTransformationMatrix(position, rotation.x, rotation.y, rotation.z, scale);
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(getTextureOffsetX(modelTexture), getTextureOffsetY(modelTexture));
        shader.loadSelected(false, false);

    }

    /**
     * Returns the X offset for a texture map.
     * 
     * @param modelTexture to get the values from
     * 
     * @return position offset of the texture
     */
    public float getTextureOffsetX(ModelTexture modelTexture) {
        int column = modelTexture.getActiveTextureIndex() % modelTexture.getNumberOfRows();
        return (column / (float) modelTexture.getNumberOfRows());
    }

    /**
     * Returns the Y offset for a texture map.
     * 
     * @param modelTexture to get the values from
     * 
     * @return position offset of the texture
     */
    public float getTextureOffsetY(ModelTexture modelTexture) {
        int row = modelTexture.getActiveTextureIndex() / modelTexture.getNumberOfRows();
        return (row / (float) modelTexture.getNumberOfRows());
    }

}
