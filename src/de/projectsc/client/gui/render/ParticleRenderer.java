/*
 * Copyright (C) 2015 
 */

package de.projectsc.client.gui.render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import de.projectsc.client.gui.models.RawModel;
import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.objects.ParticleSource;
import de.projectsc.client.gui.shaders.ParticleShader;
import de.projectsc.client.gui.tools.Loader;

public class ParticleRenderer {

    private ParticleShader shader;

    private RawModel quad;

    private Matrix4f projectionMatrix;

    private Camera camera;

    public ParticleRenderer(Loader loader, Matrix4f projectionMatrix) {
        // Just x and z vertex positions here, y is set to 0 in v.shader
        float[] vertices = {
            -1, -1,
            1, -1,
            -1, 1,
            1, -1,
            -1, 1,
            1, 1 };
        quad = loader.loadToVAO(vertices, 2);
        shader = new ParticleShader();
        this.projectionMatrix = projectionMatrix;
    }

    /**
     * Render all particles.
     * 
     * @param particles to render
     */
    public void render(List<ParticleSource> particleSources) {

        shader.start();
        // GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        for (ParticleSource source : particleSources) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, source.getTextureAtlas());
            shader.loadPositionAttributes(camera.createViewMatrix(), projectionMatrix);
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        }

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        shader.stop();
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
