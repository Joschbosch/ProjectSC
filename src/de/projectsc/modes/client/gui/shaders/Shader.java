/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.gui.shaders;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**
 * Everything that has to do with shaders (load them, bind them).
 * 
 * @author Josch Bosch
 */
public abstract class Shader {

    private static final int ERROR_LOG_LENGTH = 500;

    private static final int FLOAT_BUFFER_SIZE = 16;

    private static final Log LOGGER = LogFactory.getLog(Shader.class);

    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(FLOAT_BUFFER_SIZE);

    private final int shaderProgram;

    private final int vertexShader;

    private final int fragmentShader;

    private final int geometryShader;

    public Shader(String vertexFile, String fragmentFile) {
        vertexShader = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
        fragmentShader = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
        shaderProgram = GL20.glCreateProgram();
        geometryShader = -1;
        GL20.glAttachShader(shaderProgram, vertexShader);
        GL20.glAttachShader(shaderProgram, fragmentShader);
        bindAttributes();
        GL20.glLinkProgram(shaderProgram);
        GL20.glValidateProgram(shaderProgram);
        getAllUniformLocations();
    }

    public Shader(String vertexFile, String fragmentFile, String geometryFile) {
        vertexShader = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
        fragmentShader = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
        geometryShader = loadShader(geometryFile, GL32.GL_GEOMETRY_SHADER);
        shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertexShader);
        GL20.glAttachShader(shaderProgram, fragmentShader);
        GL20.glAttachShader(shaderProgram, geometryShader);
        bindAttributes();
        GL20.glLinkProgram(shaderProgram);
        GL20.glValidateProgram(shaderProgram);
        getAllUniformLocations();
    }

    /**
     * Start shader.
     */
    public void start() {
        GL20.glUseProgram(shaderProgram);
    }

    /**
     * Stop shader.
     */
    public void stop() {
        GL20.glUseProgram(0);
    }

    protected void bindAttribute(int attribute, String varNAme) {
        GL20.glBindAttribLocation(shaderProgram, attribute, varNAme);
    }

    protected abstract void bindAttributes();

    private static int loadShader(String file, int type) {
        String shaderSource = "";

        try {
            shaderSource = FileUtils.readFileToString(new File(Shader.class.getResource("/shader/" + file).toURI()));
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Couild not load shader: ", e);
        }
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            LOGGER.error("Could not compile shader" + GL20.glGetShaderInfoLog(shaderID, ERROR_LOG_LENGTH));
        }
        return shaderID;
    }

    /**
     * Dispose everything.
     */
    public void dispose() {
        stop();
        GL20.glDetachShader(shaderProgram, vertexShader);
        GL20.glDetachShader(shaderProgram, fragmentShader);
        GL20.glDeleteShader(fragmentShader);
        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteProgram(shaderProgram);
    }

    protected abstract void getAllUniformLocations();

    /**
     * @param uniformName in shader.
     * @return location int of given variable.
     */
    public int getUniformLocation(String uniformName) {
        return GL20.glGetUniformLocation(shaderProgram, uniformName);
    }

    protected void loadFloat(int location, float value) {
        GL20.glUniform1f(location, value);
    }

    protected void loadInt(int location, int value) {
        GL20.glUniform1i(location, value);
    }

    protected void loadVector(int location, Vector3f value) {
        GL20.glUniform3f(location, value.x, value.y, value.z);
    }

    protected void loadVector(int location, Vector4f value) {
        GL20.glUniform4f(location, value.x, value.y, value.z, value.w);
    }

    protected void loadVector(int location, Vector2f value) {
        GL20.glUniform2f(location, value.x, value.y);
    }

    protected void loadBoolean(int location, boolean value) {
        float toLoad = 0;
        if (value) {
            toLoad = 1;
        }
        loadFloat(location, toLoad);
    }

    protected void loadMatrix(int location, Matrix4f matrix) {
        matrix.store(matrixBuffer);
        matrixBuffer.flip();
        GL20.glUniformMatrix4(location, false, matrixBuffer);
    }

}
