/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.shaders;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Everything that has to do with shaders (load them, bind them).
 * 
 * @author Josch Bosch
 */
public abstract class Shader {

    private static final int FLOAT_BUFFER_SIZE = 16;

    private static final int MINUS_ONE = -1;

    private static final Log LOGGER = LogFactory.getLog(Shader.class);

    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(FLOAT_BUFFER_SIZE);

    private int shaderProgram;

    private int vertexShader;

    private int fragmentShader;

    public Shader(String vertexShaderFileName, String fragmentShaderFileName) {
        boolean done = loadProgram(vertexShaderFileName, fragmentShaderFileName);
        if (done) {
            getAllUniformLocations();
        }
    }

    /**
     * Loads the given shader files into a program.
     * 
     * @param vertexShaderFileName name of vshader
     * @param fragmentShaderFileName name of fshader
     * @return true, if shader were loaded
     */
    public boolean loadProgram(String vertexShaderFileName, String fragmentShaderFileName) {
        vertexShader = loadShader(GL_VERTEX_SHADER, vertexShaderFileName);
        fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentShaderFileName);
        if (vertexShader != 0 - 1 && fragmentShader != 0 - 1) {
            shaderProgram = createProgram();
            return true;
        }
        return false;
    }

    /**
     * Start shader program.
     */
    public void start() {
        GL20.glUseProgram(shaderProgram);
    }

    /**
     * 
     * Stop shader program.
     */
    public void stop() {
        GL20.glUseProgram(0);
    }

    protected abstract void bindAttributes();

    protected void bindAttribute(int attribute, String variableName) {
        GL20.glBindAttribLocation(shaderProgram, attribute, variableName);
    }

    /**
     * Dispose all shader.
     */
    public void dispose() {
        stop();
        GL20.glDetachShader(shaderProgram, vertexShader);
        GL20.glDetachShader(shaderProgram, fragmentShader);
        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);
    }

    private int createProgram() {
        int prog = linkProgram();
        return prog;

    }

    private int loadShader(int shaderType, String shaderFilename) {
        String shaderCode;
        try {
            shaderCode = FileUtils.readFileToString(new File(Shader.class.getResource("/shader/" + shaderFilename).toURI()));
            return compileShader(shaderType, shaderCode);
        } catch (IOException | URISyntaxException e) {
            LOGGER.error(e.getStackTrace());
        }
        return MINUS_ONE;
    }

    private int compileShader(int shaderType, String shaderCode) {
        int shader = glCreateShader(shaderType);

        glShaderSource(shader, shaderCode);
        glCompileShader(shader);

        int status = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (status == GL_FALSE) {
            glDeleteShader(shader);
            LOGGER.error("Could not load shader: " + status);
            return 0 - 1;
        }

        return shader;
    }

    private int linkProgram() {
        int program = glCreateProgram();
        return linkProgram(program);
    }

    protected int getUniformLocation(String name) {
        return GL20.glGetUniformLocation(shaderProgram, name);
    }

    protected abstract void getAllUniformLocations();

    protected void loadFloat(int location, float value) {
        GL20.glUniform1f(location, value);
    }

    protected void loadVector(int location, Vector3f value) {
        GL20.glUniform3f(location, value.x, value.y, value.z);
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

    private int linkProgram(int program) {
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        bindAttributes();
        glLinkProgram(program);
        int status = glGetProgrami(program, GL_LINK_STATUS);
        if (status == GL_FALSE) {
            glDeleteProgram(program);
        }
        return program;
    }

}
