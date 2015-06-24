/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.gui.models.RawModel;
import de.projectsc.gui.objects.Camera;
import de.projectsc.gui.shaders.WaterShader;
import de.projectsc.gui.terrain.water.WaterFrameBuffers;
import de.projectsc.gui.terrain.water.WaterTile;
import de.projectsc.gui.tools.Loader;
import de.projectsc.gui.tools.Maths;

/**
 * Renders water tiles.
 *
 * @author Josch Bosch
 */
public class WaterRenderer {

    private RawModel quad;

    private final WaterShader shader;

    private final WaterFrameBuffers fbos;

    public WaterRenderer(Loader loader, Matrix4f projectionMatrix, WaterFrameBuffers fbos) {
        this.shader = new WaterShader();
        this.fbos = fbos;
        shader.start();
        shader.loadTextures();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
        setUpVAO(loader);
    }

    /**
     * 
     * Render water tiles.
     * 
     * @param water tiles to render
     * @param camera current camera
     */
    public void render(List<WaterTile> water, Camera camera) {
        prepareRender(camera);
        for (WaterTile tile : water) {
            Matrix4f modelMatrix = Maths.createTransformationMatrix(
                new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0,
                WaterTile.TILE_SIZE);
            shader.loadModelMatrix(modelMatrix);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());
        }
        unbind();
    }

    private void prepareRender(Camera camera) {
        shader.start();
        shader.loadViewMatrix(camera);
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getReflectionTexture());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionTexture());
    }

    private void unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    private void setUpVAO(Loader loader) {
        // Just x and z vectex positions here, y is set to 0 in v.shader
        float[] vertices = { 0 - 1, 0 - 1, 0 - 1, 1, 1, 0 - 1, 1, 0 - 1, 0 - 1, 1, 1, 1 };
        quad = loader.loadToVAO(vertices, 2);
    }

    /**
     * Dispose method.
     *
     */
    public void dispose() {
        shader.dispose();
    }

}
