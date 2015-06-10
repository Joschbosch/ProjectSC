/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.states;

import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import de.projectsc.core.data.content.Map;
import de.projectsc.core.data.content.TileType;
import de.projectsc.core.data.messages.GUIMessage;
import de.projectsc.core.data.messages.GUIMessageConstants;
import de.projectsc.gui.InputData;
import de.projectsc.gui.Window;

/**
 * 
 * State when the game is running (drawing the map etc.).
 * 
 * @author Josch Bosch
 */
public class StateGameRunning implements State {

    private static final Log LOGGER = LogFactory.getLog(StateGameRunning.class);

    private static final int MINUS_ONE = -1;

    private static final GUIState STATE = GUIState.GAME;

    private Map currentMap;

    private final Window window;

    private final BlockingQueue<GUIMessage> outgoingQueue;

    private int textureID;

    public StateGameRunning(Window window, BlockingQueue<GUIMessage> outgoingQueue) {
        this.window = window;
        this.outgoingQueue = outgoingQueue;
    }

    @Override
    public void initialize() {
        //
        // glMatrixMode(GL_PROJECTION);
        // glOrtho(0, window.getWidth(), window.getHeight(), 0, -1, 1); // 2D projection matrix
        // glMatrixMode(GL_MODELVIEW);
        //
        // glClearColor(0, 1, 0, 0); // Green clear color
        //
        //

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void update() {

    }

    @Override
    public void render() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        // GL11.glOrtho(0, window.getWidth(), 0, window.getHeight(), MINUS_ONE, 1);
        GL11.glOrtho(0, window.getWidth(), window.getHeight(), 0, -1, 1); // 2D projection matrix
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        // Clear the screen and depth buffer
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        if (currentMap != null) {
            int tileSize = 30;
            for (int i = 0; i < currentMap.getWidth(); i++) {
                for (int j = 0; j < currentMap.getHeight(); j++) {
                    // set the color of the quad (R,G,B,A)
                    if (currentMap.getTileAt(i, j).getType() == TileType.NOTHING) {
                        GL11.glColor3f(1.0f, 1.0f, 1.0f);
                    } else {
                        GL11.glColor3f(0.0f, 1.0f, 0.0f);
                    }
                    {
                        GL11.glBegin(GL11.GL_QUADS);
                        GL11.glVertex2f(i * tileSize, j * tileSize);
                        GL11.glVertex2f(i * tileSize + tileSize, j * tileSize);
                        GL11.glVertex2f(i * tileSize + tileSize, j * tileSize
                            + tileSize);
                        GL11.glVertex2f(i * tileSize, j * tileSize + tileSize);
                        GL11.glEnd();
                    }
                }
            }
        }
        GraphicsUtils.drawText("Puh!", 0, 0);
    }

    public void setCurrentMap(Map map) {
        this.currentMap = map;
    }

    @Override
    public void handleInput(BlockingQueue<InputData> inputQueue) {
        InputData input = inputQueue.poll();
        try {
            while (input != null) {
                if (input.getType() == InputData.TYPE_KEY) {
                    handleKeyInput(input);
                } else if (input.getType() == InputData.TYPE_MOUSE_KEY) {
                    handleMouseButtonInput(input);
                } else if (input.getType() == InputData.TYPE_MOUSE_SCROLL) {
                    handleMouseScrollInput(input);
                }

                input = inputQueue.poll();
            }
        } catch (InterruptedException e) {
            LOGGER.error("Input handling error : ", e);
        }
    }

    private void handleMouseScrollInput(InputData input) throws InterruptedException {
        if (input.getAction() != 0) {
            outgoingQueue.put(new GUIMessage(GUIMessageConstants.START_GAME, null));
        }
    }

    private void handleMouseButtonInput(InputData input) throws InterruptedException {
        if (input.getKeyOrButton() == GLFW.GLFW_MOUSE_BUTTON_1) {
            if (input.getAction() == GLFW.GLFW_PRESS) {
            }
            if (input.getAction() == GLFW.GLFW_RELEASE) {
                outgoingQueue.put(new GUIMessage(GUIMessageConstants.START_GAME, null));
            }
        }
    }

    private void handleKeyInput(InputData input) throws InterruptedException {
        if (input.getKeyOrButton() == GLFW.GLFW_KEY_D) {
            if (input.getAction() == GLFW.GLFW_REPEAT) {
                outgoingQueue.put(new GUIMessage(GUIMessageConstants.START_GAME, null));
            } else if (input.getAction() == GLFW.GLFW_PRESS) {
                outgoingQueue.put(new GUIMessage(GUIMessageConstants.START_GAME, null));
            } else if (input.getAction() == GLFW.GLFW_RELEASE) {
                outgoingQueue.put(new GUIMessage(GUIMessageConstants.START_GAME, null));
            }
        }
    }
}
