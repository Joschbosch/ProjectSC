/*
 * Copyright (C) 2015
 */

package de.projectsc.client.gui.render;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.util.vector.Matrix4f;

import de.projectsc.client.gui.models.RawModel;
import de.projectsc.client.gui.objects.Camera;
import de.projectsc.client.gui.objects.ParticleEmitter;
import de.projectsc.client.gui.shaders.ParticleShader;
import de.projectsc.client.gui.tools.Loader;

/**
 * Renderer for particles.
 * 
 * @author Josch Bosch
 */
public class ParticleRenderer {

    private final ParticleShader shader;

    private final RawModel quad;

    private final Matrix4f projectionMatrix;

    private Camera camera;

    private final FloatBuffer positionAndSizeBuffer;

    private final int positionVBOId;

    private final ByteBuffer colorBuffer;

    private final int colorVBOId;

    public ParticleRenderer(Matrix4f projectionMatrix) {
        this.projectionMatrix = projectionMatrix;

        shader = new ParticleShader();
        // Just x and z vertex positions here, y is set to 0 in v.shader
        float[] vertices = {
            -1, -1, 0,
            1, -1, 0,
            -1, 1, 0,
            1, 1, 0 };
        quad = Loader.loadToVAO(vertices, 3);

        positionAndSizeBuffer = BufferUtils.createFloatBuffer(ParticleEmitter.MAX_PARTICLES_PER_SOURCE * 4);
        positionVBOId = Loader.createStreamVBO(positionAndSizeBuffer);

        colorBuffer = BufferUtils.createByteBuffer(ParticleEmitter.MAX_PARTICLES_PER_SOURCE * 4);
        colorVBOId = Loader.createStreamVBO(colorBuffer);

    }

    /**
     * Render all particles.
     * 
     * @param emitters list of emitters to render
     */
    public void render(List<ParticleEmitter> emitters) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        shader.start();
        shader.loadPositionAttributes(camera.createViewMatrix(), projectionMatrix);
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        for (ParticleEmitter emitter : emitters) {
            updateBuffer(emitter);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionVBOId);
            GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 0, 0);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorVBOId);
            GL20.glVertexAttribPointer(2, 4, GL11.GL_UNSIGNED_BYTE, true, 0, 0);

            GL33.glVertexAttribDivisor(0, 0);
            GL33.glVertexAttribDivisor(1, 1);
            GL33.glVertexAttribDivisor(2, 1);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, emitter.getTextureAtlas());
            shader.loadTexture(0);
            shader.loadOffset(emitter.getOffset());
            shader.loadNumberOfRows(emitter.getNumberOfRows());
            GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, 4, emitter.getParticleCount());
        }
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        shader.stop();
    }

    private void updateBuffer(ParticleEmitter p) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionVBOId);
        positionAndSizeBuffer.clear();
        positionAndSizeBuffer.put(p.getPositionBuffer());
        positionAndSizeBuffer.flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionAndSizeBuffer, GL15.GL_STREAM_DRAW);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, positionAndSizeBuffer);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorVBOId);
        colorBuffer.clear();
        colorBuffer.put(p.getColorBuffer());
        colorBuffer.flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer, GL15.GL_STREAM_DRAW);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, colorBuffer);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
