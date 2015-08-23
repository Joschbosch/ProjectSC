/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.client.gui;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import de.projectsc.client.core.GUI;
import de.projectsc.client.core.elements.Snapshot;
import de.projectsc.client.core.messages.ClientMessage;
import de.projectsc.client.core.states.ClientStates;
import de.projectsc.client.gui.states.GameRunning;
import de.projectsc.client.gui.states.State;

/**
 * Core class for the GUI.
 * 
 * @author Josch Bosch
 */
public class GUICore implements GUI {

    private static final int MAX_FRAME_RATE = 60;

    private static final int HEIGHT = 1024;

    private static final int WIDTH = 1280;

    private static final Log LOGGER = LogFactory.getLog(GUICore.class);

    private boolean running;

    private State currentState;

    private GameRunning game;

    public GUICore(BlockingQueue<ClientMessage> outgoingQueue) {}

    @Override
    public void load() {
        game = new GameRunning();
        game.initialize();
    }

    @Override
    public boolean init() {
        LOGGER.debug("Initialize GUI core");
        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.setTitle("Project SC");
            Display.setVSyncEnabled(true);
            Display.create();
        } catch (LWJGLException e) {
            LOGGER.error(e.getStackTrace());
        }
        LOGGER.debug("Opened window ");
        LOGGER.debug("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
        return true;
    }

    @Override
    public void render(ClientStates state, long elapsedTime, Snapshot snapshot) {
        if (Display.isCloseRequested()) {
            LOGGER.debug("Send close request and close down");
        }
        if (currentState != null) {
            switch (state) {
            case FINISHED:
                break;
            case GAME_LOBBY:
                break;
            case LOADING:
                break;
            case LOBBY:
                break;
            case PAUSED:
                break;
            case RUNNING:
                ((GameRunning) currentState).render(elapsedTime);
                break;
            default:
                break;

            }
        }
        Display.sync(MAX_FRAME_RATE);
        Display.update();
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void changeState(ClientStates state) {
        switch (state) {
        case FINISHED:
            break;
        case GAME_LOBBY:
            break;
        case LOADING:
            break;
        case LOBBY:
            break;
        case PAUSED:
            break;
        case RUNNING:
            currentState = game;
            break;
        default:
            break;

        }
        currentState.initialize();
    }

    @Override
    public Queue<ClientMessage> readInput() {
        return null;
    }

}
