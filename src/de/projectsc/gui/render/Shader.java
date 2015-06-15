/*
 * Copyright (C) 2015 
 */

package de.projectsc.gui.render;

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
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.opengl.GL20;

public class Shader {

    private static final Log LOGGER = LogFactory.getLog(Shader.class);

    public final static ProgramData loadProgram(String vertexShaderFileName, String fragmentShaderFileName) {
        ArrayList<Integer> shaderList = new ArrayList<>();
        shaderList.add(loadShader(GL_VERTEX_SHADER, vertexShaderFileName));
        shaderList.add(loadShader(GL_FRAGMENT_SHADER, fragmentShaderFileName));
        ProgramData data = new ProgramData();
        data.theProgram = createProgram(shaderList);
        data.modelToWorldMatrixUnif = GL20.glGetUniformLocation(data.theProgram, "modelToWorldMatrix");
        data.worldToCameraMatrixUnif = GL20.glGetUniformLocation(data.theProgram, "worldToCameraMatrix");
        data.cameraToClipMatrixUnif = GL20.glGetUniformLocation(data.theProgram, "cameraToClipMatrix");
        data.baseColorUnif = GL20.glGetUniformLocation(data.theProgram, "baseColor");
        return data;
    }

    public static int createProgram(ArrayList<Integer> shaders) {
        try {
            int prog = linkProgram(shaders);
            return prog;
        } finally {
            for (Integer shader : shaders) {
                glDeleteShader(shader);
            }
        }
    }

    public static int loadShader(int shaderType, String shaderFilename) {
        String shaderCode;
        try {
            shaderCode = FileUtils.readFileToString(new File(Shader.class.getResource("/shader/" + shaderFilename).toURI()));
            return compileShader(shaderType, shaderCode);
        } catch (IOException | URISyntaxException e) {
            LOGGER.error(e.getStackTrace());
        }
        return -1;
    }

    public static int compileShader(int shaderType, String shaderCode) {
        int shader = glCreateShader(shaderType);

        glShaderSource(shader, shaderCode);
        glCompileShader(shader);

        int status = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (status == GL_FALSE) {
            glDeleteShader(shader);
            LOGGER.error("Could not load shader: " + status);
        }

        return shader;
    }

    public static int linkProgram(ArrayList<Integer> shaders) {
        int program = glCreateProgram();
        return linkProgram(program, shaders);
    }

    private static int linkProgram(int program, ArrayList<Integer> shaders) {
        for (Integer shader : shaders) {
            glAttachShader(program, shader);
        }

        glLinkProgram(program);

        int status = glGetProgrami(program, GL_LINK_STATUS);
        if (status == GL_FALSE) {
            glDeleteProgram(program);
        }

        for (Integer shader : shaders) {
            glDetachShader(program, shader);
        }

        return program;
    }
}
