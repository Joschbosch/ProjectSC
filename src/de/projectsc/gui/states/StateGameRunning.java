/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.states;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDepthRange;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.content.Map;
import de.projectsc.core.data.messages.GUIMessage;
import de.projectsc.gui.Camera;
import de.projectsc.gui.Drawable;
import de.projectsc.gui.GameFont;
import de.projectsc.gui.content.GUICommand;
import de.projectsc.gui.content.MiniMap;
import de.projectsc.gui.render.Mesh;
import de.projectsc.gui.render.ProgramData;
import de.projectsc.gui.render.Shader;
import de.projectsc.gui.tiles.TileMap;

/**
 * 
 * State when the game is running (drawing the map etc.).
 * 
 * @author Josch Bosch
 */
public class StateGameRunning implements State {

    private static final Log LOGGER = LogFactory.getLog(StateGameRunning.class);

    private static final GUIState STATE = GUIState.GAME;

    private Map currentMap;

    private final BlockingQueue<GUIMessage> outgoingQueue;

    private List<Drawable> drawables = new LinkedList<>();

    private MiniMap minimap;

    private final BlockingQueue<GUICommand> drawableQueue = new LinkedBlockingQueue<>();

    private Camera camera;

    private Mesh planeMesh;

    private ProgramData uniformColor;

    private int vsId;

    private int fsId;

    private int pId;

    public StateGameRunning(BlockingQueue<GUIMessage> outgoingQueue) {
        this.outgoingQueue = outgoingQueue;
        camera = new Camera();
    }

    @Override
    public void initialize() {
        TileMap.loadTileSet();
        GameFont.loadFonts();
        minimap = new MiniMap(null, drawableQueue, this);
        drawables.add(minimap);

        initOpenGL();
        planeMesh = new Mesh("UnitPlane.xml");
    }

    private void initOpenGL() {
        initializeProgram();

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CW);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDepthFunc(GL_LEQUAL);
        glDepthRange(0.0f, 1.0f);
        glEnable(GL_DEPTH_CLAMP);
        reshape(Display.getWidth(), Display.getHeight());
    }

    protected void reshape(int w, int h) {
        float zNear = 1.0f;
        float zFar = 1000.0f;
        Stack<Matrix4f> persMatrixStack = new Stack<>();
        Matrix4f persMatrix = new Matrix4f();
        persMatrixStack.push(persMatrix);
        float range = (float) (Math.tan(Math.toRadians(45.0f / 2.0f)) * zNear);
        float left = -range * (w / (float) h);
        float right = range * (w / (float) h);
        float bottom = -range;
        float top = range;
        persMatrix.m00 = (2.0f * zNear) / (right - left);
        persMatrix.m11 = (2.0f * zNear) / (top - bottom);
        persMatrix.m22 = -(zFar + zNear) / (zFar - zNear);
        persMatrix.m23 = -1.0f;
        persMatrix.m32 = -(2.0f * zFar * zNear) / (zFar - zNear);
        persMatrix.m33 = 0;
        FloatBuffer matrix44Buffer = BufferUtils.createFloatBuffer(16);
        persMatrix.store(matrix44Buffer);
        matrix44Buffer.flip();
        glUseProgram(uniformColor.theProgram);
        glUniformMatrix4(uniformColor.cameraToClipMatrixUnif, false, matrix44Buffer);
        glUseProgram(0);

        glViewport(0, 0, w, h);
    }

    private void initializeProgram() {
        uniformColor = Shader.loadProgram("PosOnlyWorldTransform.vert", "ColorUniform.frag");
        setupShaders();
    }

    private int loadShader(String filename, int type) {
        int shaderID = 0;
        String shaderSource = "";

        try {
            shaderSource = FileUtils.readFileToString(new File(getClass().getResource("/shader/" + filename).toURI()));
        } catch (IOException | URISyntaxException e) {
        }
        shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);

        if (GL20.glGetShader(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.err.println("Could not compile shader.");
            System.exit(-1);
        }

        return shaderID;
    }

    private void setupShaders() {
        // Load the vertex shader
        vsId = this.loadShader("tilemap.vert", GL20.GL_VERTEX_SHADER);
        // Load the fragment shader
        fsId = this.loadShader("tilemap.frag", GL20.GL_FRAGMENT_SHADER);

        // Create a new shader program that links both shaders
        pId = GL20.glCreateProgram();
        GL20.glAttachShader(pId, vsId);
        GL20.glAttachShader(pId, fsId);

        // Color information will be attribute 1
        GL20.glBindAttribLocation(pId, 3, "in_Color");
        // Textute information will be attribute 2
        GL20.glBindAttribLocation(pId, 1, "in_TextureCoord");

        GL20.glLinkProgram(pId);
        GL20.glValidateProgram(pId);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void render(long elapsedTime) {
        update();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClearDepth(1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        final Vector3f camPos = camera.resolveCamPosition();
        FloatBuffer matrix44Buffer = BufferUtils.createFloatBuffer(16);
        Matrix4f camMatrix = camera.calcLookAtMatrix(camPos, camera.getCamTarget(), new Vector3f(0.0f, 1.0f, 0.0f));
        camMatrix.store(matrix44Buffer);
        matrix44Buffer.flip();
        glUseProgram(uniformColor.theProgram);
        glUniformMatrix4(uniformColor.worldToCameraMatrixUnif, false, matrix44Buffer);
        glUseProgram(0);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                // Render the ground plane.
                GL20.glUseProgram(pId);
                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, TileMap.getTilesetID());
                Matrix4f modelMatrix = new Matrix4f();
                modelMatrix.setIdentity();
                modelMatrix.translate(new Vector3f(i, 0, j));
                modelMatrix.m00 = 1.0f;
                modelMatrix.m11 = 1.0f;
                modelMatrix.m22 = 1.0f;
                modelMatrix.store(matrix44Buffer);
                matrix44Buffer.flip();
                glUseProgram(uniformColor.theProgram);
                glUniformMatrix4(uniformColor.modelToWorldMatrixUnif, false, matrix44Buffer);
                GL20.glUniform4f(uniformColor.baseColorUnif, 1f, i > 0 ? 1 : 0, 0f, 1.0f);
                planeMesh.render();
            }
        }
        glUseProgram(0);
    }

    @Override
    public void update() {

    }

    @Override
    public void terminate() {

    }

    public void setCurrentMap(Map map) {
        if (minimap != null) {
            minimap.setCurrentMap(map);
            if (currentMap == null) {
                minimap.setCurrentPosition(Display.getWidth() - minimap.getWidth(), Display.getHeight() - minimap.getHeight());
            }
        }
        this.currentMap = map;
    }

    @Override
    public void handleInput(long elapsedTime) {

        handleKeyInput(elapsedTime);

        GUICommand command = drawableQueue.poll();
        while (command != null) {
            if (command.getMessage().equals(GUICommand.CHANGE_LOCATION)) {
            }
            command = drawableQueue.poll();

        }
    }

    private void handleKeyInput(long elapsedTime) {
        camera.updatePosition(elapsedTime);
    }
}
