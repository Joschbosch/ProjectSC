/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.client.gui.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

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

    private static final String IMAGE_FILETYPE = "PNG";

    private static final float MIPMAP_BIAS = 0f;

    private static final float ANISOTROPIC_FILTERING_AMOUNT = 4;

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
     * @param normals of each face
     * @param indices for vao
     * 
     * @return the model with the vao
     */
    public static RawModel loadToVAO(float[] positions, float[] textureCoordinates, float[] normals, int[] indices, int[] jointIndicesArr,
        float[] weightsArr) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoordinates);
        storeDataInAttributeList(2, 3, normals);
        float[] tangents = new float[positions.length];
        Arrays.fill(tangents, 0);
        storeDataInAttributeList(3, 3, tangents);
        storeDataInAttributeList(4, 4, weightsArr);
        storeDataInAttributeList(5, 4, jointIndicesArr);
        unbind();
        return new RawModel(vaoID, indices.length);
    }

    public static RawModel loadToVAO(FloatBuffer positions, FloatBuffer textureCoordinates, FloatBuffer normals, int[] indices,
        int[] jointIndicesArr,
        FloatBuffer weightsArr) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoordinates);
        storeDataInAttributeList(2, 3, normals);
        float[] tangents = new float[positions.capacity()];
        Arrays.fill(tangents, 0);
        storeDataInAttributeList(3, 3, tangents);
        storeDataInAttributeList(4, 4, weightsArr);
        storeDataInAttributeList(5, 4, jointIndicesArr);
        unbind();
        return new RawModel(vaoID, indices.length);
    }

    /**
     * Loading given data positions into a VAO.
     * 
     * @param positions to load
     * @param textureCoordinates to apply to the model
     * @param normals of each face
     * @param tangents of the faces
     * @param indices for vao
     * 
     * @return the model with the vao
     */
    public static RawModel loadToVAO(float[] positions, float[] textureCoordinates, float[] normals, float[] tangents, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoordinates);
        storeDataInAttributeList(2, 3, normals);
        storeDataInAttributeList(3, 3, tangents);
        unbind();
        return new RawModel(vaoID, indices.length);
    }

    /**
     * Loading given data positions into a VAO.
     * 
     * @param positions to load
     * @param textureCoordinates to apply to the model
     * @param normals of each face
     * @param tangents of the faces
     * @param indices for vao
     * 
     * @return the model with the vao
     */
    public static RawModel loadToVAO(FloatBuffer positions, FloatBuffer textureCoordinates, FloatBuffer normals, FloatBuffer tangents,
        int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoordinates);
        storeDataInAttributeList(2, 3, normals);
        storeDataInAttributeList(3, 3, tangents);
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
     * Creates a VBO for streaming data.
     * 
     * @param floatCount size
     * @return id of vbo
     */
    public static int createEmptyVBO(int floatCount) {
        int vbo = GL15.glGenBuffers();
        VBOS.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_STREAM_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    /**
     * Add instances in VBO.
     * 
     * @param vao in which the vbo is
     * @param vbo to add to
     * @param attribute to add
     * @param dataSize size to add
     * @param instanceDataLength length
     * @param offset of data
     */
    public static void addInstancesAttribute(int vao, int vbo, int attribute, int dataSize, int instanceDataLength, int offset) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL30.glBindVertexArray(vao);
        GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, instanceDataLength * 4, offset * 4);
        GL33.glVertexAttribDivisor(attribute, 1);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    /**
     * Updates the given VBO.
     * 
     * @param vbo to update
     * @param data to write
     * @param buffer to write to.
     */
    public static void updateVbo(int vbo, float[] data, FloatBuffer buffer) {
        buffer.clear();
        buffer.put(data);
        buffer.flip();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * 4, GL15.GL_STATIC_DRAW);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

    }

    /**
     * Load texture with PNG format.
     * 
     * @param location of the texture
     * @return texture index
     */
    public static int loadTextureFromSchema(String location) {
        if (textureMap.containsKey(location)) {
            return textureMap.get(location);
        } else {
            int textureID = loadTexture(Loader.class.getResourceAsStream(location), IMAGE_FILETYPE);
            textureMap.put(location, textureID);
            return textureID;
        }
    }

    /**
     * Load texture with PNG format.
     * 
     * @param filename to load
     * @return location of texture
     */
    public static int loadTexture(BufferedImage img, String name, String type) {
        try {
            if (textureMap.containsKey(name)) {
                return textureMap.get(name);
            } else {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIO.write(img, type, stream);
                byte[] content = stream.toByteArray();
                InputStream in = new BufferedInputStream(new ByteInputStream(content, content.length));
                int textureID = loadTexture(in, type);
                textureMap.put(name, textureID);
                in.close();
                return textureID;
            }
        } catch (IOException e) {
            LOGGER.error("Could not convert to stream.");
            return -1;
        }
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
            int textureID = loadTexture(Loader.class.getResourceAsStream(GUIConstants.TEXTURE_ROOT + filename), IMAGE_FILETYPE);
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
                int texId = loadTexture(new FileInputStream(file), IMAGE_FILETYPE);
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
            if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
                float amount = Math.max(ANISOTROPIC_FILTERING_AMOUNT,
                    GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
            } else {
                LOGGER.info("Anisotropic filtering not supported!");
            }
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

    private static void storeDataInAttributeList(int attrNumber, int dataSize, FloatBuffer data) {
        int vboID = GL15.glGenBuffers();
        VBOS.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attrNumber, dataSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private static void storeDataInAttributeList(int attrNumber, int dataSize, int[] data) {
        int vboID = GL15.glGenBuffers();
        VBOS.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(data);
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
            TextureData data = decodeTextureFile(GUIConstants.TEXTURE_ROOT + textureFiles[i] + ".png");
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
