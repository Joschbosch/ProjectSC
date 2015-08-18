/*
 * Copyright (C) 2015
 */

package de.projectsc.client.gui.render;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
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

    private RawModel quad;

    private final Matrix4f projectionMatrix;

    private Camera camera;

    private Loader loader;

    private int billboard_vertex_buffer;

    private int particles_position_buffer;

    private int particles_color_buffer;

    private int VertexArrayID;

    public ParticleRenderer(Loader loader, Matrix4f projectionMatrix) {

        this.projectionMatrix = projectionMatrix;
        // Just x and z vertex positions here, y is set to 0 in v.shader
        float[] vertices = {
            -1, -1,
            1, -1,
            -1, 1,
            1, -1,
            -1, 1,
            1, 1 };
        shader = new ParticleShader();
        // quad = loader.loadToVAO(vertices, 2);
        // float[] data = new float[ParticleEmitter.MAX_PARTICLES_PER_SOURCE * 4];
        // FloatBuffer positionsBuffer = loader.storeDataInFloatBuffer(data);
        //
        // quad.addStreamingBuffer(loader.createStreamingFloatVBO(quad.getVaoID(), 1, positionsBuffer, 4));
        // byte[] data2 = new byte[ParticleEmitter.MAX_PARTICLES_PER_SOURCE * 3];
        // ByteBuffer buffer2 = loader.storeDataInByteBuffer(data2);
        // quad.addStreamingBuffer(loader.createStreamingByteVBO(quad.getVaoID(), 2, buffer2, 4));
        //
        // this.projectionMatrix = projectionMatrix;
        // this.loader = loader;
        VertexArrayID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(VertexArrayID);

        billboard_vertex_buffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, billboard_vertex_buffer);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices);
        buffer.flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        particles_position_buffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, particles_position_buffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, ParticleEmitter.MAX_PARTICLES_PER_SOURCE * 4 * Float.SIZE, GL15.GL_STREAM_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        particles_color_buffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, particles_color_buffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, ParticleEmitter.MAX_PARTICLES_PER_SOURCE * 4 * Byte.SIZE, GL15.GL_STREAM_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

    }

    /**
     * Render all particles.
     * 
     * @param particles to render
     */
    public void render(List<ParticleEmitter> particleSources) {

        ParticleEmitter e = particleSources.get(0);
        // GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, particles_position_buffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, ParticleEmitter.MAX_PARTICLES_PER_SOURCE * 4 * Float.SIZE, GL15.GL_STATIC_DRAW);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(e.getPositionBuffer().length);
        buffer.put(e.getPositionBuffer());
        buffer.flip();
        // GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, buffer.capacity(), buffer);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, particles_color_buffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, ParticleEmitter.MAX_PARTICLES_PER_SOURCE * 4 * Byte.SIZE, GL15.GL_STATIC_DRAW);
        ByteBuffer buffer2 = BufferUtils.createByteBuffer(e.getColorBuffer().length);
        buffer2.put(e.getColorBuffer());
        buffer2.flip();
        // GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, buffer2.capacity(), buffer2);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LESS);

        shader.start();
        shader.loadPositionAttributes(camera.createViewMatrix(), projectionMatrix);
        GL30.glBindVertexArray(VertexArrayID);
        GL20.glEnableVertexAttribArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, billboard_vertex_buffer);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(1);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, particles_position_buffer);
        GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(2);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, particles_color_buffer);
        GL20.glVertexAttribPointer(2, 4, GL11.GL_UNSIGNED_BYTE, true, 0, 0);
        GL33.glVertexAttribDivisor(0, 0);
        GL33.glVertexAttribDivisor(1, 1);
        GL33.glVertexAttribDivisor(2, 1);
        GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, 4, e.getParticleCount());
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);

        GL30.glBindVertexArray(0);
        shader.stop();
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
