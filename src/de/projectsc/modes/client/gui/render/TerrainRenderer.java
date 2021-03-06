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
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.utils.Maths;
import de.projectsc.modes.client.gui.models.RawModel;
import de.projectsc.modes.client.gui.objects.terrain.TerrainModel;
import de.projectsc.modes.client.gui.shaders.TerrainShader;
import de.projectsc.modes.client.gui.textures.TerrainTexturePack;

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
     * @param toShadowSpace matrix for shadow space.
     * @param shadowDistance distance shadows are rendered
     */
    public void render(List<TerrainModel> terrains, Matrix4f toShadowSpace, float shadowDistance) {
        shader.loadToShadowSpaceMatrix(toShadowSpace);
        shader.loadlocationShadowDistance(shadowDistance);
        for (TerrainModel terrain : terrains) {
            prepareTerrain(terrain);
            loadModelMatrix(terrain);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindTexturedModel();
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

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
            Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0.0f, terrain.getZ()), 0, 0, 0, new Vector3f(1, 1, 1));
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
