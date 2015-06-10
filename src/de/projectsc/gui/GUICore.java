/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui;

import static de.projectsc.core.data.messages.GUIMessageConstants.CLOSE_DOWN_GUI;
import static de.projectsc.core.data.messages.GUIMessageConstants.NEW_MAP;
import static de.projectsc.core.data.messages.GUIMessageConstants.START_GAME;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.data.content.Map;
import de.projectsc.core.data.messages.GUIMessage;
import de.projectsc.gui.states.GUIState;
import de.projectsc.gui.states.State;
import de.projectsc.gui.states.StateGameRunning;

/**
 * Core class for the GUI.
 * 
 * @author Josch Bosch
 */
public class GUICore implements Runnable {

    private static final int HEIGHT = 768;

    private static final int WIDTH = 1024;

    private static final Log LOGGER = LogFactory.getLog(GUICore.class);

    private static final int TARGET_FPS = 75;

    private static final float TARGET_UPS = 30;

    private static java.util.Map<GUIState, State> stateMap = new HashMap<GUIState, State>();

    private boolean running;

    private Timer timer;

    private Window window;

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
        LOGGER.debug("Initialize done, starting gui loop ...");
        try {
            startGameLoop();
        } catch (InterruptedException e) {
            LOGGER.error("GUI Core: ", e);
        }
        dispose();
    }

    private void dispose() {

    }

    private void init() {
        LOGGER.debug("Initialize");
        window = new Window(WIDTH, HEIGHT, "ProjectSC", false);
        LOGGER.debug("Opened window ");
        this.timer = new Timer();
        running = true;
    }

    private void startGameLoop() throws InterruptedException {
        float delta;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;
        float alpha;
        outgoingQueue.put(new GUIMessage(START_GAME, null));
        stateMap.put(GUIState.GAME, new StateGameRunning(window));
        currentState = stateMap.get(GUIState.GAME);
        LOGGER.debug("Starting Game");
        while (running) {
            if (window.isClosing()) {
                outgoingQueue.put(new GUIMessage(CLOSE_DOWN_GUI, null));
                running = false;
                LOGGER.debug("Send close requrest and close down");
            }
            retreiveCoreMessages();
            delta = timer.getDelta();
            accumulator += delta;
            input();
            while (accumulator >= interval) {
                update();
                timer.updateUPS();
                accumulator -= interval;
            }
            alpha = accumulator / interval;
            render(alpha);
            timer.updateFPS();
            timer.update();
            window.update();
            if (!window.isVSyncEnabled()) {
                sync(TARGET_FPS);
            }
        }
    }

    private void retreiveCoreMessages() {
        while (!incomingQueue.isEmpty()) {
            GUIMessage msg = incomingQueue.poll();
            if (msg.getMessage().equals(NEW_MAP)) {
                LOGGER.debug("Retrieve new map!");
                map = (Map) msg.getData();
                if (currentState instanceof StateGameRunning) {
                    ((StateGameRunning) currentState).setCurrentMap(map);
                }
            } else if (msg.getMessage().equals(CLOSE_DOWN_GUI)) {
                LOGGER.debug("Closing down");
                running = false;

            }
        }
    }

    private void sync(int targetFps) {

    }

    private void render(float alpha) {
        window.render(currentState);
    }

    private void update() {
        currentState.update();
    }

    private void input() {

    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        start();
    }
}
