/*
 * Copyright (C) 2015
 */

package de.projectsc.client.gui.render;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;

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

public class ParticleRenderer {

    private final ParticleShader shader;

    private final RawModel quad;

    private final Matrix4f projectionMatrix;

    private Camera camera;

    private Loader loader;

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
        float[] data = new float[ParticleEmitter.MAX_PARTICLES_PER_SOURCE * 4];
        FloatBuffer positionsBuffer = loader.storeDataInFloatBuffer(data);

        quad.addStreamingBuffer(loader.createStreamingFloatVBO(quad.getVaoID(), 1, positionsBuffer, 4));
        byte[] data2 = new byte[ParticleEmitter.MAX_PARTICLES_PER_SOURCE * 3];
        ByteBuffer buffer2 = loader.storeDataInByteBuffer(data2);
        quad.addStreamingBuffer(loader.createStreamingByteVBO(quad.getVaoID(), 2, buffer2, 4));

        shader = new ParticleShader();
        this.projectionMatrix = projectionMatrix;
        this.loader = loader;
    }

    /**
     * Render all particles.
     * 
     * @param particles to render
     */
    public void render(List<ParticleEmitter> particleSources) {

        ParticleEmitter source = particleSources.get(0);
        GL30.glBindVertexArray(quad.getVaoID());
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, quad.getBuffer(0));
        FloatBuffer positionBuffer = loader.storeDataInFloatBuffer(source.getPositionBuffer());
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionBuffer.capacity() * Float.SIZE, GL15.GL_STREAM_DRAW);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, positionBuffer.capacity(), positionBuffer);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

        shader.start();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, source.getTextureAtlas());
        shader.loadPositionAttributes(camera.createViewMatrix(), projectionMatrix);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, quad.getBuffer(0));
        GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(2);
        GL20.glVertexAttribPointer(2, 4, GL11.GL_UNSIGNED_BYTE, true, 0, 0);
        GL33.glVertexAttribDivisor(0, 0);
        GL33.glVertexAttribDivisor(1, 1);
        GL33.glVertexAttribDivisor(2, 1);

        GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, 4, source.getParticleCount());

        // GL30.glBindVertexArray(quad.getVaoID());
        // GL20.glEnableVertexAttribArray(0);
        // GL20.glEnableVertexAttribArray(1);

        // GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 0, 0);
        // GL20.glEnableVertexAttribArray(2);
        // //
        // GL11.glDisable(GL11.GL_DEPTH_TEST);
        // for (ParticleSource source : particleSources) {
        //
        // FloatBuffer positionBuffer = loader.storeDataInFloatBuffer(source.getPositionBuffer());
        // GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, positionBuffer.capacity() * Float.SIZE, GL15.GL_STREAM_DRAW);
        // GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, positionBuffer.capacity(), positionBuffer);
        // GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 0, 0);
        //
        // GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, quad.getBuffer(1));
        // ByteBuffer colorBuffer = loader.storeDataInByteBuffer(source.getColorBuffer());
        // GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, colorBuffer.capacity() * Byte.SIZE, GL15.GL_STREAM_DRAW);
        // GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, colorBuffer.capacity(), colorBuffer);
        // GL20.glVertexAttribPointer(2, 4, GL11.GL_UNSIGNED_BYTE, true, 0, 0);
        //
        // GL33.glVertexAttribDivisor(0, 0);
        // GL33.glVertexAttribDivisor(1, 1);
        // GL33.glVertexAttribDivisor(2, 1);
        // GL13.glActiveTexture(GL13.GL_TEXTURE0);
        // System.out.println(source.getTextureAtlas());
        // GL11.glBindTexture(GL11.GL_TEXTURE_2D, source.getTextureAtlas());
        // shader.loadPositionAttributes(camera.createViewMatrix(), projectionMatrix);
        // GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, 4, source.getParticleCount());
        // //
        // }
        //
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(1);
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
