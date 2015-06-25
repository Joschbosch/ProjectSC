/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.gui.content;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.input.Mouse;

import de.projectsc.client.core.data.content.Map;
import de.projectsc.client.gui.OverlayMoveable;
import de.projectsc.client.gui.states.GameRunning;

/**
 * Class for the minimap overlay.
 * 
 * @author Josch Bosch
 */
public class MiniMap implements OverlayMoveable {

    private static final int WINDOW_BORDER = 15;

    private static final int POINT_SIZE = 2;

    private static final int DEFAULT_SIZE = POINT_SIZE * 100;

    private static final Log LOGGER = LogFactory.getLog(MiniMap.class);

    private Map currentMap;

    private final int[] cornerPosition;

    private int width;

    private int height;

    @SuppressWarnings("unused")
    private final BlockingQueue<GUICommand> outputQueue;

    private final boolean moving = false;

    private int movingOffsetX;

    private int movingOffsetY;

    @SuppressWarnings("unused")
    private final GameRunning stateGameRunning;

    public MiniMap(Map currentMap, BlockingQueue<GUICommand> drawableQueue, GameRunning stateGameRunning) {
        LOGGER.debug("Initialize minimap ... ");
        cornerPosition = new int[2];
        if (currentMap != null) {
            width = currentMap.getWidth() * POINT_SIZE;
            height = currentMap.getHeight() * POINT_SIZE + WINDOW_BORDER;
        } else {
            width = DEFAULT_SIZE;
            height = DEFAULT_SIZE + WINDOW_BORDER;
        }
        this.outputQueue = drawableQueue;
        this.stateGameRunning = stateGameRunning;
        LOGGER.debug("Minimap initialized. ");
    }

    @Override
    public void render() {
        glColor3f(1, 0, 0);
        glBegin(GL_QUADS);
        glVertex2f(cornerPosition[0], cornerPosition[1]);
        glVertex2f(cornerPosition[0], cornerPosition[1] + WINDOW_BORDER);
        glVertex2f(cornerPosition[0] + width, cornerPosition[1] + WINDOW_BORDER);
        glVertex2f(cornerPosition[0] + width, cornerPosition[1]);
        glEnd();
        for (int i = 0; i < currentMap.getWidth(); i++) {
            for (int j = 0; j < currentMap.getHeight(); j++) {
                float[] color = currentMap.getTileAt(i, j, 0).getType().getColor();
                glColor3f(color[0], color[1], color[2]);
                glBegin(GL_QUADS);
                glVertex2f(cornerPosition[0] + (i * POINT_SIZE), cornerPosition[1] + (j * POINT_SIZE) + WINDOW_BORDER);
                glVertex2f(cornerPosition[0] + (i * POINT_SIZE), cornerPosition[1] + (j * POINT_SIZE) + POINT_SIZE + WINDOW_BORDER);
                glVertex2f(cornerPosition[0] + POINT_SIZE + (i * POINT_SIZE),
                    cornerPosition[1] + POINT_SIZE + (j * POINT_SIZE) + WINDOW_BORDER);
                glVertex2f(cornerPosition[0] + POINT_SIZE + (i * POINT_SIZE), cornerPosition[1] + (j * POINT_SIZE) + WINDOW_BORDER);
                glEnd();
            }
        }

        // int rectX = cornerPosition[0] + POINT_SIZE;
        // int rectY = cornerPosition[1] + POINT_SIZE;
        // int rectWidth = Display.getWidth() / TileMap.TILE_SIZE * POINT_SIZE;
        // int rectHeight = Display.getHeight() / TileMap.TILE_SIZE * POINT_SIZE;
        // glColor3f(1, 1, 1);
        // glBegin(GL11.GL_LINES);
        // glVertex2f(rectX, rectY + WINDOW_BORDER);
        // glVertex2f(rectX + rectWidth, rectY + WINDOW_BORDER);
        // glVertex2f(rectX + rectWidth, rectY + WINDOW_BORDER);
        // glVertex2f(rectX + rectWidth, rectY + rectHeight + WINDOW_BORDER);
        // glVertex2f(rectX + rectWidth, rectY + rectHeight + WINDOW_BORDER);
        // glVertex2f(rectX, rectY + rectHeight + WINDOW_BORDER);
        // glVertex2f(rectX, rectY + rectHeight + WINDOW_BORDER);
        // glVertex2f(rectX, rectY + WINDOW_BORDER);
        // glEnd();
    }

    /**
     * Set new map and new minimap bounds.
     * 
     * @param currentMap to draw
     */
    public void setCurrentMap(Map currentMap) {
        this.currentMap = currentMap;
        if (currentMap != null) {
            width = currentMap.getWidth() * POINT_SIZE;
            height = currentMap.getHeight() * POINT_SIZE;
        } else {
            width = DEFAULT_SIZE;
            height = DEFAULT_SIZE;
        }
    }

    @Override
    public boolean isAtPostion(int x, int y) {
        if (x >= cornerPosition[0] && x <= cornerPosition[0] + width
            && y >= cornerPosition[1] && y <= cornerPosition[1] + height + WINDOW_BORDER) {
            return true;
        }

        return false;
    }

    /**
     * Sets the current position to (x,y).
     * 
     * @param x coordinate
     * @param y coordinate
     */
    public void setCurrentPosition(int x, int y) {
        cornerPosition[0] = x;
        cornerPosition[1] = y;
    }

    @Override
    public void handleInput() {
        // if (data.getType() == InputData.TYPE_MOUSE_POSITION) {
        //
        // }
        // if (data.getType() == InputData.TYPE_MOUSE_KEY) {
        // int relativeX = (int) currentCursorPosition[0] - cornerPosition[0];
        // int relativeY = (int) currentCursorPosition[1] - cornerPosition[1];
        //
        // if (data.getKeyOrButton() == GLFW.GLFW_MOUSE_BUTTON_1) {
        // if (data.getAction() == GLFW.GLFW_PRESS) {
        // if (relativeY < WINDOW_BORDER) {
        // moving = true;
        // movingOffsetX = relativeX;
        // movingOffsetY = relativeY;
        // } else {
        // try {
        // outputQueue.put(new GUICommand(GUICommand.CHANGE_LOCATION, new int[] { relativeX /
        // pointSize,
        // (relativeY - WINDOW_BORDER) / pointSize }));
        // } catch (InterruptedException e) {
        // LOGGER.error(e.getStackTrace());
        // }
        // }
        // }
        // if (data.getAction() == GLFW.GLFW_RELEASE) {
        // moving = false;
        // }
        // }
        // }

    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean isMoving() {
        return moving;
    }

    @Override
    public void move() {
        cornerPosition[0] = Mouse.getX() - movingOffsetX;
        cornerPosition[1] = Mouse.getY() - movingOffsetY;
    }
}
