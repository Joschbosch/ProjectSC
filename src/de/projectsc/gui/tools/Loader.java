/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.gui.tools;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import de.projectsc.gui.models.RawModel;

/**
 * Class for loading mesh files.
 * 
 * @author Josch Bosch
 */
public class Loader {

    private static final float MIPMAP_BIAS = 0.5f;

    private static final Log LOGGER = LogFactory.getLog(Loader.class);

    private final List<Integer> vaos = new ArrayList<>();

    private final List<Integer> vbos = new ArrayList<>();

    private final List<Integer> textures = new ArrayList<>();

    /**
     * Loading given data positions into a VAO.
     * 
     * @param positions to load
     * @param textureCoordinates to apply to the model
     * @param normals of each face
     * @param indices for vao
     * 
     * @return the model with the vao
     */
    public RawModel loadToVAO(float[] positions, float[] textureCoordinates, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoordinates);
        storeDataInAttributeList(2, 3, normals);
        unbind();
        return new RawModel(vaoID, indices.length);
    }

    /**
     * Loads just a vertex positions array to a VAO and returns the model for it.
     * 
     * @param positions to load
     * @return the model
     */
    public RawModel loadToVAO(float[] positions) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, 2, positions);
        unbind();
        return new RawModel(vaoID, positions.length / 2);
    }

    private int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    /**
     * load texture from the given file.
     * 
     * @param filename of the texture.
     * @return texture position
     */
    public int loadTexture(String filename) {
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture("PNG", Loader.class.getResourceAsStream("/graphics/" + filename));
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, MIPMAP_BIAS);
        } catch (IOException e) {
            LOGGER.error("Could not load texture: " + filename + " :", e);
        }
        int textureID = texture.getTextureID();
        textures.add(textureID);
        return textureID;
    }

    private void storeDataInAttributeList(int attrNumber, int dataSize, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attrNumber, dataSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboId = GL15.glGenBuffers();
        vbos.add(vboId);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    /**
     * Dispose everything.
     */
    public void dispose() {
        for (int vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        for (int vbo : vbos) {
            GL15.glDeleteBuffers(vbo);
        }
        for (int texture : textures) {
            GL11.glDeleteTextures(texture);
        }

    }

    private void unbind() {
        GL30.glBindVertexArray(0);
    }
}
