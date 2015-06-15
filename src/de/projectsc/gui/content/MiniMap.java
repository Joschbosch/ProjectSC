/*
 * CopcornerPosition[1]right (C)WINDOW_BORDER15 
 */

package de.projectsc.gui.content;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import de.projectsc.core.data.content.Map;
import de.projectsc.gui.MoveableDrawable;
import de.projectsc.gui.states.StateGameRunning;
import de.projectsc.gui.tiles.TileMap;

public class MiniMap implements MoveableDrawable {

    private static final int WINDOW_BORDER = 15;

    private static final int pointSize = 2;

    private static final int DEFAULT_SIZE = pointSize * 100;

    private static final Log LOGGER = LogFactory.getLog(MiniMap.class);

    private Map currentMap;

    private int[] cornerPosition;

    private int width;

    private int height;

    private BlockingQueue<GUICommand> outputQueue;

    private boolean moving = false;

    private int movingOffsetX;

    private int movingOffsetY;

    private StateGameRunning stateGameRunning;

    public MiniMap(Map currentMap, BlockingQueue<GUICommand> drawableQueue, StateGameRunning stateGameRunning) {
        cornerPosition = new int[2];
        if (currentMap != null) {
            width = currentMap.getWidth() * pointSize;
            height = currentMap.getHeight() * pointSize + WINDOW_BORDER;
        } else {
            width = DEFAULT_SIZE;
            height = DEFAULT_SIZE + WINDOW_BORDER;
        }
        this.outputQueue = drawableQueue;
        this.stateGameRunning = stateGameRunning;
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
                glVertex2f(cornerPosition[0] + (i * pointSize), cornerPosition[1] + (j * pointSize) + WINDOW_BORDER);
                glVertex2f(cornerPosition[0] + (i * pointSize), cornerPosition[1] + (j * pointSize) + pointSize + WINDOW_BORDER);
                glVertex2f(cornerPosition[0] + pointSize + (i * pointSize), cornerPosition[1] + pointSize + (j * pointSize) + WINDOW_BORDER);
                glVertex2f(cornerPosition[0] + pointSize + (i * pointSize), cornerPosition[1] + (j * pointSize) + WINDOW_BORDER);
                glEnd();
            }
        }

        int rectX = cornerPosition[0] + pointSize;
        int rectY = cornerPosition[1] + pointSize;
        int rectWidth = Display.getWidth() / TileMap.TILE_SIZE * pointSize;
        int rectHeight = Display.getHeight() / TileMap.TILE_SIZE * pointSize;
        glColor3f(1, 1, 1);
        glBegin(GL11.GL_LINES);
        glVertex2f(rectX, rectY + WINDOW_BORDER);
        glVertex2f(rectX + rectWidth, rectY + WINDOW_BORDER);
        glVertex2f(rectX + rectWidth, rectY + WINDOW_BORDER);
        glVertex2f(rectX + rectWidth, rectY + rectHeight + WINDOW_BORDER);
        glVertex2f(rectX + rectWidth, rectY + rectHeight + WINDOW_BORDER);
        glVertex2f(rectX, rectY + rectHeight + WINDOW_BORDER);
        glVertex2f(rectX, rectY + rectHeight + WINDOW_BORDER);
        glVertex2f(rectX, rectY + WINDOW_BORDER);
        glEnd();
    }

    /**
     * Set new map and new minimap bounds.
     * 
     * @param currentMap to draw
     */
    public void setCurrentMap(Map currentMap) {
        this.currentMap = currentMap;
        if (currentMap != null) {
            width = currentMap.getWidth() * pointSize;
            height = currentMap.getHeight() * pointSize;
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
        // outputQueue.put(new GUICommand(GUICommand.CHANGE_LOCATION, new int[] { relativeX / pointSize,
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
