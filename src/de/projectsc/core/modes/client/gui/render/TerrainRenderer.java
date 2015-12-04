/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.core.modes.client.gui.render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.modes.client.gui.models.RawModel;
import de.projectsc.core.modes.client.gui.shaders.TerrainShader;
import de.projectsc.core.modes.client.gui.terrain.TerrainModel;
import de.projectsc.core.modes.client.gui.textures.TerrainTexturePack;
import de.projectsc.core.modes.client.gui.tools.Maths;

/**
 * Render terrain data with own shader.
 * 
 * @author Josch Bosch
 */
public class TerrainRenderer {

    private final TerrainShader shader;

    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.connectTextureUnits();
        shader.stop();
    }

    /**
     * Render given terrains.
     * 
     * @param terrains to render
     */
    public void render(List<TerrainModel> terrains) {
        for (TerrainModel terrain : terrains) {
            prepareTerrain(terrain);
            loadModelMatrix(terrain);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindTexturedModel();
        }
    }

    private void prepareTerrain(TerrainModel terrain) {
        RawModel model = terrain.getModel();
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        bindTextures(terrain);
        shader.loadShineValues(1, 0);

    }

    private void bindTextures(TerrainModel terrain) {
        TerrainTexturePack texturePack = terrain.getTexture();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getRTexture().getTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getGTexture().getTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBTexture().getTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
    }

    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private void loadModelMatrix(TerrainModel terrain) {
        Matrix4f transformationMatrix =
            Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0.0f, terrain.getZ()), 0, 0, 0, 1);
        shader.loadTransformationMatrix(transformationMatrix);
    }
}