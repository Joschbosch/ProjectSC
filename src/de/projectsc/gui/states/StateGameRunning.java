/*
 * Copyright (C) 2006-2015 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.projectsc.gui.states;

import org.lwjgl.opengl.GL11;

import de.projectsc.core.data.Map;
import de.projectsc.core.data.TileType;
import de.projectsc.gui.Window;

public class StateGameRunning implements State {

    public static final GUIState state = GUIState.GAME;

    private Map currentMap;

    private final Window window;

    public StateGameRunning(Window window) {
        this.window = window;
    }

    @Override
    public void initialize() {

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
        GL11.glOrtho(0, 800, 0, 600, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        // Clear the screen and depth buffer
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        if (currentMap != null) {
            int tileSize = window.getWidth() / currentMap.getWidth();
            for (int i = 0; i < currentMap.getWidth(); i++) {
                for (int j = 0; j < currentMap.getHeight(); j++) {
                    // set the color of the quad (R,G,B,A)
                    if (currentMap.getTileAt(i, j).getType() == TileType.NOTHING) {
                        GL11.glColor3f(1.0f, 1.0f, 1.0f);
                    } else {
                        GL11.glColor3f(0.0f, 1.0f, 0.0f);
                    }
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

    public void setCurrentMap(Map map) {
        this.currentMap = map;
    }
}
