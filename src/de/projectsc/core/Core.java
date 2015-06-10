/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.core;

import static de.projectsc.core.data.messages.GUIMessageConstants.CLOSE_DOWN_GUI;
import static de.projectsc.core.data.messages.GUIMessageConstants.NEW_MAP;
import static de.projectsc.core.data.messages.GUIMessageConstants.START_GAME;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.algorithms.MapGenerator;
import de.projectsc.core.data.content.Map;
import de.projectsc.core.data.messages.GUIMessage;
import de.projectsc.core.data.messages.NetworkMessage;

/**
 * Core class for the client.
 * 
 * @author Josch Bosch
 */
public class Core implements Runnable {

    private static final int SLEEP_TIME = 50;

    private static final String ERROR_IN_CORE = "Error in Core: ";

    private static final Log LOGGER = LogFactory.getLog(Core.class);

    private BlockingQueue<GUIMessage> guiIncomingQueue;

    private BlockingQueue<GUIMessage> guiOutgoingQueue;

    private BlockingQueue<NetworkMessage> networkIncomingQueue;

    private BlockingQueue<NetworkMessage> networkOutgoingQueue;

    private boolean shutdown;

    public Core() {
        guiIncomingQueue = new LinkedBlockingQueue<>();
        guiOutgoingQueue = new LinkedBlockingQueue<>();
        networkIncomingQueue = new LinkedBlockingQueue<>();
        networkOutgoingQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        LOGGER.debug("Starting core ... ");

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (!shutdown) {
                    try {
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        LOGGER.error(ERROR_IN_CORE, e);
                    }

                    workGUI();
                }
            }
        }).start();

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (!shutdown) {
                    try {
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        LOGGER.error(ERROR_IN_CORE, e);
                    }
                    workNetwork();
                }
            }
        }).start();
        LOGGER.debug("Core initialized");
    }

    private void workNetwork() {

    }

    private void workGUI() {
        try {
            GUIMessage msg = guiIncomingQueue.take();
            LOGGER.debug("New Message: " + msg.getMessage());
            if (msg.getMessage().contains(START_GAME)) {
                Map m = createMap();
                guiOutgoingQueue.put(new GUIMessage(NEW_MAP, m));
            } else if (msg.getMessage().contains(CLOSE_DOWN_GUI)) {
                guiOutgoingQueue.offer(new GUIMessage(CLOSE_DOWN_GUI, null));
                networkOutgoingQueue.offer(new NetworkMessage(CLOSE_DOWN_GUI, null));
                shutdown = true;
                LOGGER.debug("Shutting down");
            }
        } catch (InterruptedException e) {
            LOGGER.error(ERROR_IN_CORE, e);
        }
    }

    // /*********** TEST CODE **************/
    private static int i = 0;

    private Map createMap() {
        Map m = new Map(10 * 10, 10 * 10);
        MapGenerator.createRandomMap(i++, m);
        return m;
    }

    public BlockingQueue<GUIMessage> getGuiIncomingQueue() {
        return guiIncomingQueue;
    }

    public void setGuiIncomingQueue(BlockingQueue<GUIMessage> guiIncomingQueue) {
        this.guiIncomingQueue = guiIncomingQueue;
    }

    public BlockingQueue<GUIMessage> getGuiOutgoingQueue() {
        return guiOutgoingQueue;
    }

    public void setGuiOutgoingQueue(BlockingQueue<GUIMessage> guiOutgoingQueue) {
        this.guiOutgoingQueue = guiOutgoingQueue;
    }

    public BlockingQueue<NetworkMessage> getNetworkIncomingQueue() {
        return networkIncomingQueue;
    }

    public void setNetworkIncomingQueue(BlockingQueue<NetworkMessage> networkIncomingQueue) {
        this.networkIncomingQueue = networkIncomingQueue;
    }

    public BlockingQueue<NetworkMessage> getNetworkOutgoingQueue() {
        return networkOutgoingQueue;
    }

    public void setNetworkOutgoingQueue(BlockingQueue<NetworkMessage> networkOutgoingQueue) {
        this.networkOutgoingQueue = networkOutgoingQueue;
    }

}
