/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui;

import static de.projectsc.core.data.messages.GUIMessageConstants.CLOSE_DOWN_GUI;
import static de.projectsc.core.data.messages.GUIMessageConstants.NEW_MAP;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import de.projectsc.core.data.content.Map;
import de.projectsc.core.data.messages.GUIMessage;
import de.projectsc.core.data.messages.GUIMessageConstants;
import de.projectsc.gui.states.GUIState;
import de.projectsc.gui.states.State;
import de.projectsc.gui.states.StateGameRunning;

/**
 * Core class for the GUI.
 * 
 * @author Josch Bosch
 */
public class GUICore implements Runnable {

    private static final int HEIGHT = 700;

    private static final int WIDTH = 700;

    private static final Log LOGGER = LogFactory.getLog(GUICore.class);

    private static final int TARGET_FPS = 500;

    private static final float TARGET_UPS = 300;

    private static java.util.Map<GUIState, State> stateMap = new HashMap<GUIState, State>();

    private boolean running;

    private Timer timer;

    private Map map;

    private final BlockingQueue<GUIMessage> outgoingQueue;

    private final BlockingQueue<GUIMessage> incomingQueue;

    private State currentState;

    public GUICore(BlockingQueue<GUIMessage> outgoingQueue, BlockingQueue<GUIMessage> incomingQueue) {
        this.outgoingQueue = outgoingQueue;
        this.incomingQueue = incomingQueue;
    }

    /**
     * This should be called to initialize and start the game.
     */
    public void start() {
        LOGGER.debug("Starting GUI ...");
        init();
        try {
            outgoingQueue.put(new GUIMessage(GUIMessageConstants.START_GAME, null));
            LOGGER.debug("Initialize done, send start game message ...");
            stateMap.put(GUIState.GAME, new StateGameRunning(outgoingQueue));
            currentState = stateMap.get(GUIState.GAME);
            while (incomingQueue.isEmpty()) {
                Thread.sleep(10);
            }
            retreiveCoreMessages();
        } catch (InterruptedException e1) {
            LOGGER.error("Could not send start Message", e1);
        }
        dispose();
    }

    private void dispose() {

    }

    private void init() {
        LOGGER.debug("Initialize");
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
        this.timer = new Timer();
        running = true;
    }

    private void startGameLoop() throws InterruptedException {
        LOGGER.debug("Starting Game");
        timer.init();
        while (running) {
            if (Display.isCloseRequested()) {
                outgoingQueue.put(new GUIMessage(CLOSE_DOWN_GUI, null));
                running = false;
                LOGGER.debug("Send close requrest and close down");
            }
            retreiveCoreMessages();
            currentState.handleInput(timer.getDelta());
            currentState.render(timer.getDelta());
            timer.updateFPS();
            Display.sync(60);
            Display.update();
        }
    }

    private void retreiveCoreMessages() {
        while (!incomingQueue.isEmpty()) {
            GUIMessage msg = incomingQueue.poll();
            if (msg.getMessage().equals(NEW_MAP)) {
                LOGGER.debug("Retrieve new map!");
                if (map == null) {
                    LOGGER.debug("Starting game!");

                    map = (Map) msg.getData();
                    if (currentState instanceof StateGameRunning) {
                        ((StateGameRunning) currentState).setCurrentMap(map);
                        ((StateGameRunning) currentState).initialize();
                    }
                    try {
                        startGameLoop();
                    } catch (InterruptedException e) {
                        LOGGER.error("GUI Core: ", e);
                    }
                } else {
                    map = (Map) msg.getData();
                    if (currentState instanceof StateGameRunning) {
                        ((StateGameRunning) currentState).setCurrentMap(map);
                    }
                }
            } else if (msg.getMessage().equals(CLOSE_DOWN_GUI)) {
                LOGGER.debug("Closing down");
                running = false;

            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        start();
    }
}
