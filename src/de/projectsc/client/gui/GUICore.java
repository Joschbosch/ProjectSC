/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.gui;

import static de.projectsc.core.data.messages.MessageConstants.CLOSE_DOWN;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import de.projectsc.client.gui.states.GUIState;
import de.projectsc.client.gui.states.GameRunning;
import de.projectsc.client.gui.states.Menue;
import de.projectsc.client.gui.states.State;
import de.projectsc.core.WorldEntity;

/**
 * Core class for the GUI.
 * 
 * @author Josch Bosch
 */
public class GUICore implements Runnable {

    private static final int MAX_FRAME_RATE = 120;

    private static final int HEIGHT = 1024;

    private static final int WIDTH = 1280;

    private static final Log LOGGER = LogFactory.getLog(GUICore.class);

    private static java.util.Map<GUIState, State> stateMap = new HashMap<GUIState, State>();

    private boolean running;

    private Timer timer;

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
        stateMap.put(GUIState.GAME, new GameRunning(outgoingQueue));
        stateMap.put(GUIState.MAIN_MENUE, new Menue());
        currentState = stateMap.get(GUIState.MAIN_MENUE);
    }

    private void startRenderingLoop() throws InterruptedException {
        LOGGER.debug("Starting GUI ...");
        timer.init();
        running = true;
        outgoingQueue.offer(new GUIMessage(GUIMessageConstants.GUI_INITIALIZED, null));
        while (running) {
            if (Display.isCloseRequested()) {
                outgoingQueue.put(new GUIMessage(CLOSE_DOWN, null));
                running = false;
                LOGGER.debug("Send close request and close down");
            }
            retreiveCoreMessages();
            int delta = timer.getDelta();
            currentState.handleInput(delta);
            currentState.render(delta);
            timer.updateFPS();
            Display.sync(MAX_FRAME_RATE);
            Display.update();
        }
    }

    @SuppressWarnings("unchecked")
    private void retreiveCoreMessages() {
        GUIMessage msg;
        msg = incomingQueue.poll();
        if (msg != null) {
            if (msg.getMessage().equals(GUIMessageConstants.INIT_GAME)) {
                LOGGER.debug("Initialize game!");
                currentState = stateMap.get(GUIState.GAME);
                if (currentState instanceof GameRunning) {
                    ((GameRunning) currentState).initialize();
                    for (WorldEntity e : ((Map<Integer, WorldEntity>) msg.getData()).values()) {
                        ((GameRunning) currentState).addWorldEntity(e);
                    }
                }
                outgoingQueue.offer(new GUIMessage(GUIMessageConstants.GAME_GUI_INITIALIZED, null));

                LOGGER.debug("Starting game!");
            } else if (msg.getMessage().equals(CLOSE_DOWN)) {
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
        try {
            startRenderingLoop();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            LOGGER.error("Error in rendering loop: ", e);
        }
    }
}
