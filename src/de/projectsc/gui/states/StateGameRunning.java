/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.states;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.content.Map;
import de.projectsc.core.data.messages.GUIMessage;
import de.projectsc.gui.Camera;
import de.projectsc.gui.GameFont;
import de.projectsc.gui.Overlay;
import de.projectsc.gui.content.GUICommand;
import de.projectsc.gui.content.MiniMap;
import de.projectsc.gui.render.Loader;
import de.projectsc.gui.render.Mesh;
import de.projectsc.gui.render.Model;
import de.projectsc.gui.render.RawModel;
import de.projectsc.gui.shaders.Shader;
import de.projectsc.gui.shaders.StaticShader;
import de.projectsc.gui.shaders.UniformColorShader;
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

    private final List<Overlay> drawables = new LinkedList<>();

    private MiniMap minimap;

    private final BlockingQueue<GUICommand> drawableQueue = new LinkedBlockingQueue<>();

    private final Camera camera;

    private Mesh planeMesh;

    private Shader uniformColor;

    private Shader staticShader;

    private Model goat;

    private final Loader loader;

    private RawModel m;

    public StateGameRunning(BlockingQueue<GUIMessage> outgoingQueue) {
        this.outgoingQueue = outgoingQueue;
        camera = new Camera();
        loader = new Loader();
    }

    @Override
    public void initialize() {
        TileMap.loadTileSet();
        GameFont.loadFonts();
        minimap = new MiniMap(null, drawableQueue, this);
        drawables.add(minimap);

        initOpenGL();
        planeMesh = new Mesh("UnitPlane.xml");

        goat = new Model("goat.obj");
        float[] vertices = { -0.5f, 0.5f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f,
            0.5f, 0.5f, 0f
        };
        int[] indices = { 0, 1, 3, 3, 1, 2 };
        m = loader.loadToVAO(vertices, indices);
    }

    private void initOpenGL() {
        initializeProgram();

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        // GL11.glFrontFace(GL11.GL_CW);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glDepthRange(0.0f, 1.0f);
        // GL11.glEnable(GL30.GL_DEPTH_CLAMP);
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
        uniformColor.start();
        glUniformMatrix4(2, false, matrix44Buffer);
        uniformColor.stop();
        glViewport(0, 0, w, h);
    }

    private void initializeProgram() {
        uniformColor = new UniformColorShader();
        staticShader = new StaticShader();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    private void renderModel(RawModel model) {
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    private void newRendering() {
        renderModel(m);
    }

    @Override
    public void render(long elapsedTime) {
        update();
        oldRendering();
        newRendering();

    }

    private void oldRendering() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClearDepth(1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //
        final Vector3f camPos = camera.resolveCamPosition();
        FloatBuffer matrix44Buffer = BufferUtils.createFloatBuffer(16);
        Matrix4f camMatrix = camera.calcLookAtMatrix(camPos, camera.getCamTarget(), new Vector3f(0.0f, 1.0f, 0.0f));
        camMatrix.store(matrix44Buffer);
        matrix44Buffer.flip();
        uniformColor.start();
        glUniformMatrix4(1, false, matrix44Buffer);

        for (int i = 0; i < currentMap.getWidth(); i++) {
            for (int j = 0; j < currentMap.getHeight(); j++) {
                // Render the ground plane.
                Matrix4f modelMatrix = new Matrix4f();
                modelMatrix.setIdentity();
                modelMatrix.translate(new Vector3f(i, 0, j));
                modelMatrix.m00 = 1.0f;
                modelMatrix.m11 = 1.0f;
                modelMatrix.m22 = 1.0f;
                modelMatrix.store(matrix44Buffer);
                matrix44Buffer.flip();

                uniformColor.start();
                glUniformMatrix4(0, false, matrix44Buffer);
                float[] color = currentMap.getTileAt(i, j, 0).getType().getColor();
                GL20.glUniform4f(3, color[0], color[1], color[2], 1);
                planeMesh.render();
                uniformColor.stop();
            }
        }
        // GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        // Render the ground plane.6
        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.setIdentity();
        modelMatrix.translate(new Vector3f(0, 0, 0));
        modelMatrix.scale(new Vector3f(10, 10, 10));
        modelMatrix.store(matrix44Buffer);
        matrix44Buffer.flip();
        uniformColor.start();
        glUniformMatrix4(0, false, matrix44Buffer);
        GL20.glUniform4f(3, 1, 0, 0, 1);
        // planeMesh.render();
        goat.render();
        uniformColor.stop();
    }

    @Override
    public void update() {

    }

    @Override
    public void terminate() {
        loader.dispose();
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
