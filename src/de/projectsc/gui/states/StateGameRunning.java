/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.states;

import java.awt.Color;
import java.awt.Font;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import de.projectsc.core.data.content.Map;
import de.projectsc.core.data.messages.GUIMessage;
import de.projectsc.core.data.messages.GUIMessageConstants;
import de.projectsc.gui.GameFont;
import de.projectsc.gui.GraphicsUtils;
import de.projectsc.gui.InputData;
import de.projectsc.gui.Window;
import de.projectsc.gui.tiles.TileSetStore;

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

    private final Window window;

    private final BlockingQueue<GUIMessage> outgoingQueue;

    public StateGameRunning(Window window,
        BlockingQueue<GUIMessage> outgoingQueue) {
        this.window = window;
        this.outgoingQueue = outgoingQueue;
    }

    @Override
    public void initialize() {
        TileSetStore.loadTileSet();
        GameFont.loadFonts();
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
        GL11.glClearColor(0, 0, 0, 0);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, window.getWidth(), window.getHeight(), 0, 0 - 1, 1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        // Clear the screen and depth buffer
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        final int tileSize = 32;
        if (currentMap != null) {
            for (int i = 0; i < currentMap.getWidth(); i++) {
                for (int j = 0; j < currentMap.getHeight(); j++) {
                    int tile = currentMap.getTileAt(i, j).getType().getTileId();
                    GL11.glColor3f(1.0f, 1.0f, 1.0f);
                    GraphicsUtils.drawTile(i * tileSize, j * tileSize, tileSize, tile);
                }
            }

            drawMiniMap(window.getWidth() - currentMap.getWidth() * 2, window.getHeight() - currentMap.getHeight() * 2, 2);
        }
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        // TESTCODE
        GraphicsUtils.drawTile(2 * tileSize, 2 * tileSize, tileSize, 2 * 63 + 1);
        GraphicsUtils.drawText("Yippie!", textX++, 1 * 100, GameFont.getFont(GameFont.GLOBAL).deriveFont(Font.BOLD, 50), Color.RED);
        if (textX > window.getWidth()) {
            textX = 0;
        }
    }

    // TESTCODE
    private int textX = 0;

    private void drawMiniMap(int x, int y, int pointSize) {
        for (int i = 0; i < currentMap.getWidth(); i++) {
            for (int j = 0; j < currentMap.getHeight(); j++) {
                float[] color = currentMap.getTileAt(i, j).getType().getColor();
                GL11.glColor3f(color[0], color[1], color[2]);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glVertex2f(x + (i * pointSize), y + (j * pointSize));
                GL11.glVertex2f(x + (i * pointSize), y + (j * pointSize) + pointSize);
                GL11.glVertex2f(x + pointSize + (i * pointSize), y + pointSize + (j * pointSize));
                GL11.glVertex2f(x + pointSize + (i * pointSize), y + (j * pointSize));
                GL11.glEnd();
            }
        }
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

    private void handleMouseScrollInput(InputData input)
        throws InterruptedException {
        if (input.getAction() != 0) {
            outgoingQueue.put(new GUIMessage(GUIMessageConstants.START_GAME,
                null));
        }
    }

    private void handleMouseButtonInput(InputData input)
        throws InterruptedException {
        if (input.getKeyOrButton() == GLFW.GLFW_MOUSE_BUTTON_1) {
            if (input.getAction() == GLFW.GLFW_PRESS) {
                outgoingQueue.put(new GUIMessage(
                    GUIMessageConstants.START_GAME, null));
            }
            if (input.getAction() == GLFW.GLFW_RELEASE) {
                outgoingQueue.put(new GUIMessage(
                    GUIMessageConstants.START_GAME, null));
            }
        }
    }

    private void handleKeyInput(InputData input) throws InterruptedException {
        if (input.getKeyOrButton() == GLFW.GLFW_KEY_D) {
            if (input.getAction() == GLFW.GLFW_REPEAT) {
                outgoingQueue.put(new GUIMessage(
                    GUIMessageConstants.START_GAME, null));
            } else if (input.getAction() == GLFW.GLFW_PRESS) {
                outgoingQueue.put(new GUIMessage(
                    GUIMessageConstants.START_GAME, null));
            } else if (input.getAction() == GLFW.GLFW_RELEASE) {
                outgoingQueue.put(new GUIMessage(
                    GUIMessageConstants.START_GAME, null));
            }
        }
    }
}
