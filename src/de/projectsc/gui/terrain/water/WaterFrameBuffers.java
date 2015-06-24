/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.terrain.water;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import de.projectsc.gui.objects.FrameBufferObject;

/**
 * Implementation of {@link FrameBufferObject} for water.
 * 
 * @author Josch Bosch
 */
public class WaterFrameBuffers extends FrameBufferObject {

    protected static final int REFLECTION_WIDTH = 1280;

    protected static final int REFRACTION_WIDTH = 1280;

    private static final int REFLECTION_HEIGHT = 720;

    private static final int REFRACTION_HEIGHT = 720;

    private int reflectionFrameBuffer;

    private int reflectionTexture;

    private int reflectionDepthBuffer;

    private int refractionFrameBuffer;

    private int refractionTexture;

    private int refractionDepthTexture;

    public WaterFrameBuffers() {// call when loading the game
        initialiseReflectionFrameBuffer();
        initialiseRefractionFrameBuffer();
    }

    private void initialiseReflectionFrameBuffer() {
        reflectionFrameBuffer = createFrameBuffer();
        reflectionTexture = createTextureAttachment(REFLECTION_WIDTH, REFLECTION_HEIGHT);
        reflectionDepthBuffer = createDepthBufferAttachment(REFLECTION_WIDTH, REFLECTION_HEIGHT);
        unbindCurrentFrameBuffer();
    }

    private void initialiseRefractionFrameBuffer() {
        refractionFrameBuffer = createFrameBuffer();
        refractionTexture = createTextureAttachment(REFRACTION_WIDTH, REFRACTION_HEIGHT);
        refractionDepthTexture = createDepthTextureAttachment(REFRACTION_WIDTH, REFRACTION_HEIGHT);
        unbindCurrentFrameBuffer();
    }

    /**
     * Bind buffer before rendering to it.
     */
    public void bindReflectionFrameBuffer() {
        bindFrameBuffer(reflectionFrameBuffer, REFLECTION_WIDTH, REFLECTION_HEIGHT);
    }

    /**
     * Bind buffer before rendering to it.
     */
    public void bindRefractionFrameBuffer() {
        bindFrameBuffer(refractionFrameBuffer, REFRACTION_WIDTH, REFRACTION_HEIGHT);
    }

    @Override
    public void dispose() {
        GL30.glDeleteFramebuffers(reflectionFrameBuffer);
        GL11.glDeleteTextures(reflectionTexture);
        GL30.glDeleteRenderbuffers(reflectionDepthBuffer);
        GL30.glDeleteFramebuffers(refractionFrameBuffer);
        GL11.glDeleteTextures(refractionTexture);
        GL11.glDeleteTextures(refractionDepthTexture);
    }

    public int getReflectionTexture() {
        return reflectionTexture;
    }

    public int getRefractionTexture() {
        return refractionTexture;
    }

    public int getRefractionDepthTexture() {
        return refractionDepthTexture;
    }

}
