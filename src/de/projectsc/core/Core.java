/*
 * Copyright (C) 2006-2015 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.projectsc.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.algorithms.MapGenerator;
import de.projectsc.core.data.GUIMessage;
import de.projectsc.core.data.Map;
import de.projectsc.core.data.NetworkMessage;

public class Core implements Runnable {

    private static Log LOGGER = LogFactory.getLog(Core.class);

    BlockingQueue<GUIMessage> guiIncomingQueue;

    BlockingQueue<GUIMessage> guiOutgoingQueue;

    BlockingQueue<NetworkMessage> networkIncomingQueue;

    BlockingQueue<NetworkMessage> networkOutgoingQueue;

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
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        LOGGER.error("Error in Core: ", e);
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
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        LOGGER.error("Error in Core: ", e);
                    }
                    workNetwork();
                }
            }
        }).start();
        LOGGER.debug("Core initialized");

        /*********** TEST CODE **************/
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (!shutdown) {
                        Thread.sleep(1000);
                        guiIncomingQueue.put(new GUIMessage("Start Game", null));
                    }
                } catch (InterruptedException e) {
                }
            }
        }).start();
        /*********** TEST CODE **************/
    }

    private void workNetwork() {

    }

    private void workGUI() {
        try {
            GUIMessage msg = guiIncomingQueue.take();
            LOGGER.debug("New Message: " + msg.getMessage());
            if (msg.getMessage().contains("Start Game")) {
                Map m = createMap();
                guiOutgoingQueue.put(new GUIMessage("New Map", m));
            } else if (msg.getMessage().contains("Close Down")) {
                guiOutgoingQueue.offer(new GUIMessage("Close Down", null));
                networkOutgoingQueue.offer(new NetworkMessage("Close Down", null));
                shutdown = true;
                LOGGER.debug("Shutting down");
            }
        } catch (InterruptedException e) {
            LOGGER.error("Error in Core: ", e);
        }
    }

    /*********** TEST CODE **************/
    static int i = 0;

    private Map createMap() {
        Map m = new Map(100, 100);
        MapGenerator.createRandomMap(i++, m);
        return m;
    }

    /*********** TEST CODE **************/
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
