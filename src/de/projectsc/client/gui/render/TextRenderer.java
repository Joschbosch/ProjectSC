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

import de.projectsc.client.gui.models.RawModel;
import de.projectsc.client.gui.objects.ParticleEmitter;
import de.projectsc.client.gui.shaders.TextShader;
import de.projectsc.client.gui.tools.Loader;

/**
 * Renderer for billboards.
 * 
 * @author Josch Bosch
 */
public class TextRenderer {

    private final TextShader shader;

    private final RawModel quad;

    private final FloatBuffer positionAndSizeBuffer;

    private final int positionVBOId;

    private final ByteBuffer uvBuffer;

    private final int uvVBOId;

    public TextRenderer() {
        shader = new TextShader();
        float[] vertices = {
            -1, -1,
            1, -1,
            -1, 1,
            1, 1 };
        quad = Loader.loadToVAO(vertices, 2);

        positionAndSizeBuffer = BufferUtils.createFloatBuffer(ParticleEmitter.MAX_PARTICLES_PER_SOURCE * 4);
        positionVBOId = Loader.createStreamVBO(positionAndSizeBuffer);

        uvBuffer = BufferUtils.createByteBuffer(ParticleEmitter.MAX_PARTICLES_PER_SOURCE * 4);
        uvVBOId = Loader.createStreamVBO(uvBuffer);
    }

    /**
     * Render text.
     * 
     * @param text to render
     */
    public void render(List<Text2D> texts) {

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        shader.start();
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        for (Text2D text : texts) {
            updateBuffer(text);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionVBOId);
            GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 0, 0);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvVBOId);
            GL20.glVertexAttribPointer(2, 4, GL11.GL_UNSIGNED_BYTE, true, 0, 0);

            GL33.glVertexAttribDivisor(0, 0);
            GL33.glVertexAttribDivisor(1, 1);
            GL33.glVertexAttribDivisor(2, 1);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, text.getTextStyle());
            shader.loadTexture(0);
            GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, 4, text.getLength());
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

    private void updateBuffer(Text2D text) {

        float[] positions = new float[text.getLength() * 4];

        for (int i = 0; i < text.getLength(); i++) {
            positions[4 * i] = text.getX() + i * text.getSize();
            positions[4 * i + 1] = text.getX() + i * text.getSize();
            positions[4 * i + 2] = text.getX() + i * text.getSize();
            positions[4 * i + 3] = text.getX() + i * text.getSize();
        }

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionVBOId);
        positionAndSizeBuffer.clear();
        positionAndSizeBuffer.put(p.getPositionBuffer());
        positionAndSizeBuffer.flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionAndSizeBuffer, GL15.GL_STREAM_DRAW);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, positionAndSizeBuffer);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, uvVBOId);
        uvBuffer.clear();
        uvBuffer.put(p.getColorBuffer());
        uvBuffer.flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, uvBuffer, GL15.GL_STREAM_DRAW);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, uvBuffer);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

}
