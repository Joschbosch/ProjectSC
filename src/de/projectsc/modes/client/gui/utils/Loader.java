/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.client.gui.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import de.projectsc.modes.client.gui.models.RawModel;
import de.projectsc.modes.client.gui.textures.TextureData;

/**
 * Class for loading mesh files.
 * 
 * @author Josch Bosch
 */
public final class Loader {

    private static final float MIPMAP_BIAS = 0.5f;

    private static final Log LOGGER = LogFactory.getLog(Loader.class);

    private static Map<String, Integer> textureMap = new TreeMap<>();

    private static final List<Integer> VAOS = new ArrayList<>();

    private static final List<Integer> VBOS = new ArrayList<>();

    private static final List<Integer> TEXTURES = new ArrayList<>();

    private Loader() {

    }

    /**
     * Loading given data positions into a VAO.
     * 
     * @param positions to load
     * @param indices for vao
     * 
     * @return the model with the vao
     */
    public static RawModel loadToVAO(float[] positions, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        unbind();
        return new RawModel(vaoID, indices.length);
    }

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
    public static RawModel loadToVAO(float[] positions, float[] textureCoordinates, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoordinates);
        storeDataInAttributeList(2, 3, normals);
        unbind();
        return new RawModel(vaoID, indices.length);
    }

    /**
     * Loading given data positions into a VAO.
     * 
     * @param positions to load
     * @param textureCoordinates to apply to the model
     * @return the model with the vao
     */
    public static int loadToVAO(float[] positions, float[] textureCoordinates) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, 2, positions);
        storeDataInAttributeList(1, 2, textureCoordinates);
        unbind();
        return vaoID;
    }

    /**
     * Creates a VBO for streaming data.
     * 
     * @param buffer to create VBO from
     * @return id of vbo
     */
    public static int createStreamVBO(Buffer buffer) {
        int vboID = GL15.glGenBuffers();
        VBOS.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity(), GL15.GL_STREAM_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return vboID;
    }

    /**
     * Loads just a vertex positions array to a VAO and returns the model for it.
     * 
     * @param positions to load
     * @param dimensions 2 or 3
     * @return the model
     */
    public static RawModel loadToVAO(float[] positions, int dimensions) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, dimensions, positions);
        unbind();
        return new RawModel(vaoID, positions.length / dimensions);
    }

    private static int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        VAOS.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    /**
     * Load texture with PNG format.
     * 
     * @param filename to load
     * @return location of texture
     */
    public static int loadTexture(String filename) {
        if (textureMap.containsKey(filename)) {
            return textureMap.get(filename);
        } else {
            int textureID = loadTexture(Loader.class.getResourceAsStream("/graphics/" + filename), "PNG");
            textureMap.put(filename, textureID);
            return textureID;
        }
    }

    /**
     * Load texture from a file location.
     * 
     * @param file to load
     * @return location of texture
     */
    public static int loadTexture(File file) {
        if (textureMap.containsKey(file.getAbsolutePath())) {
            return textureMap.get(file.getAbsolutePath());
        } else {
            try {
                int texId = loadTexture(new FileInputStream(file), "PNG");
                textureMap.put(file.getAbsolutePath(), texId);

                return texId;
            } catch (FileNotFoundException e) {
                LOGGER.error(e);
            }
        }
        return 0 - 1;
    }

    /**
     * load texture from the given file.
     * 
     * @param fileStream of the file to load
     * @param fileType of the image
     * @return texture position
     */
    public static int loadTexture(InputStream fileStream, String fileType) {
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture(fileType, fileStream);
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, MIPMAP_BIAS);
        } catch (IOException e) {
            LOGGER.error("Could not load texture:", e);
        }
        int textureID = texture.getTextureID();
        TEXTURES.add(textureID);
        return textureID;
    }

    private static void storeDataInAttributeList(int attrNumber, int dataSize, float[] data) {
        int vboID = GL15.glGenBuffers();
        VBOS.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attrNumber, dataSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    /**
     * create Buffer.
     * 
     * @param data for the buffer
     * @return the buffer
     */
    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private static void bindIndicesBuffer(int[] indices) {
        int vboId = GL15.glGenBuffers();
        VBOS.add(vboId);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    /**
     * create Buffer.
     * 
     * @param data for the buffer
     * @return the buffer
     */
    public static ByteBuffer storeDataInByteBuffer(byte[] data) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    /**
     * Loads the cube map for the sky box.
     * 
     * @param textureFiles for all faces to load.
     * @return position
     */
    public static int loadCubeMap(String[] textureFiles) {
        int texID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

        for (int i = 0; i < textureFiles.length; i++) {
            TextureData data = decodeTextureFile("/graphics/" + textureFiles[i] + ".png");
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                data.getBuffer());
        }
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        TEXTURES.add(texID);
        return texID;
    }

    private static TextureData decodeTextureFile(String fileName) {
        int width = 0;
        int height = 0;
        ByteBuffer buffer = null;
        try {
            PNGDecoder decoder = new PNGDecoder(Loader.class.getResourceAsStream(fileName));
            width = decoder.getWidth();
            height = decoder.getHeight();
            buffer = ByteBuffer.allocateDirect(4 * width * height);
            decoder.decode(buffer, width * 4, Format.RGBA);
            buffer.flip();
        } catch (IOException e) {
            LOGGER.error("Could not load Texture " + fileName, e);
        }
        return new TextureData(width, height, buffer);
    }

    /**
     * Dispose everything.
     */
    public static void dispose() {
        for (int vao : VAOS) {
            GL30.glDeleteVertexArrays(vao);
        }
        for (int vbo : VBOS) {
            GL15.glDeleteBuffers(vbo);
        }
        for (int texture : TEXTURES) {
            GL11.glDeleteTextures(texture);
        }

    }

    private static void unbind() {
        GL30.glBindVertexArray(0);
    }

    /**
     * @param path of the texture to get
     * @return the id for open gl
     */
    public static int getTextureId(String path) {
        return textureMap.get(path);
    }

}
