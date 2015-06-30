/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.core;

import static de.projectsc.core.data.messages.MessageConstants.CLOSE_DOWN;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.GUIMessage;
import de.projectsc.client.gui.GUIMessageConstants;
import de.projectsc.core.WorldEntity;
import de.projectsc.core.data.messages.NetworkMessageConstants;

/**
 * Core class for the client.
 * 
 * @author Josch Bosch
 */
public class ClientCore implements Runnable {

    private static final int TICK_TIME = 50;

    private static final int SLEEP_TIME = 10;

    private static final String ERROR_IN_CORE = "Error in Core: ";

    private static final Log LOGGER = LogFactory.getLog(ClientCore.class);

    private BlockingQueue<GUIMessage> guiIncomingQueue;

    private BlockingQueue<GUIMessage> guiOutgoingQueue;

    private BlockingQueue<ClientMessage> networkSendQueue;

    private BlockingQueue<ClientMessage> networkReceiveQueue;

    private final AtomicBoolean guiReady = new AtomicBoolean(false);

    private final AtomicBoolean networkReady = new AtomicBoolean(false);

    private final AtomicBoolean gameGuiReady = new AtomicBoolean(false);

    private long gameTime = 0;

    private Map<Integer, WorldEntity> worldEntities;

    private boolean shutdown;

    private boolean gameStarted;

    public ClientCore() {
        guiIncomingQueue = new LinkedBlockingQueue<>();
        guiOutgoingQueue = new LinkedBlockingQueue<>();
        networkSendQueue = new LinkedBlockingQueue<>();
        networkReceiveQueue = new LinkedBlockingQueue<>();
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
        gameTime = System.currentTimeMillis();
        while (!guiReady.get() || !networkReady.get()) {
            try {
                Thread.sleep(TICK_TIME);
            } catch (InterruptedException e) {
            }
        }
        networkSendQueue.offer(new ClientMessage(ClientMessageConstants.CLIENT_READY, null));

        while (!gameStarted) {
            try {
                Thread.sleep(TICK_TIME);
            } catch (InterruptedException e) {
            }
        }

        long last = System.currentTimeMillis();
        while (!shutdown) {
            long now = System.currentTimeMillis();
            long delta = now - last;
            last = now;
            gameTime += delta;
            if (worldEntities != null) {
                for (WorldEntity e : worldEntities.values()) {
                    e.move(delta);
                }
            }
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                LOGGER.error(ERROR_IN_CORE, e);
            }
        }
    }

    private void workNetwork() {
        while (!networkReceiveQueue.isEmpty()) {
            ClientMessage message;
            message = networkReceiveQueue.poll();
            if (message != null) {
                processMessage(message);
            }
        }
    }

    private void processMessage(ClientMessage message) {
        if (message.getMessage().equals(GUIMessageConstants.INIT_GAME)) {
            LOGGER.debug("Client core received message: " + message.getMessage());
            if (message.getData() instanceof List<?>) {
                @SuppressWarnings("unchecked") List<WorldEntity> incomingEntities = (List<WorldEntity>) message.getData();
                worldEntities = new TreeMap<>();
                for (WorldEntity e : incomingEntities) {
                    worldEntities.put(e.getID(),
                        new WorldEntity(e.getID(), e.getType(), e.getModel(), e.getTexture(), new Vector3f(e.getPosition().x, e
                            .getPosition().y, e.getPosition().z), new Vector3f(e.getRotX(), e.getRotY(), e
                            .getRotZ()), e.getScale()));
                }
                guiOutgoingQueue.offer(new GUIMessage(GUIMessageConstants.INIT_GAME, worldEntities));
            }
        } else if (message.getMessage().equals(NetworkMessageConstants.NEW_LOCATION)) {
            if (message.getData() instanceof float[]) {
                float[] data = (float[]) message.getData();
                WorldEntity worldEntity = worldEntities.get((int) data[0]);
                worldEntity.setRotY(data[3]);
                worldEntity.getPosition().x = data[1];
                worldEntity.getPosition().y = 0;
                worldEntity.getPosition().x = data[2];
            } else {
                int[] data = (int[]) message.getData();
                worldEntities.get(data[0]).setCurrentTarget(new Vector3f(data[1], 0, data[2]));
                LOGGER.debug(String.format("Got new target information for entity %s: %s | %s", data[0], data[1], data[2]));
            }
        } else if (message.getMessage().equals("pong")) {
            long[] data = (long[]) message.getData();
            LOGGER.debug(String.format("Got pong with times (%s, %s) and ping = %s", data[0], data[1],
                System.currentTimeMillis()
                    - data[0]));
        } else if (message.getMessage().equals(ClientMessageConstants.CLIENT_READY)) {
            networkReady.set(true);
        } else if (message.getMessage().equals("Start game")) {
            gameStarted = true;
            gameTime = (long) message.getData();
        } else if (message.getMessage().equals(NetworkMessageConstants.GAME_TIME_UPDATE)) {
            long delta = gameTime - (long) message.getData();
            gameTime = (long) message.getData();
            LOGGER.debug("Received game time update! Delta was " + delta);
        } else {
            LOGGER.error("Message not recognized:" + message.getMessage());
        }
    }

    private void workGUI() {
        try {
            GUIMessage msg = guiIncomingQueue.take();
            LOGGER.debug("New Message: " + msg.getMessage());
            if (msg.getMessage().contains(CLOSE_DOWN)) {
                guiOutgoingQueue.offer(new GUIMessage(CLOSE_DOWN, null));
                networkSendQueue.offer(new ClientMessage(CLOSE_DOWN, null));
                shutdown = true;
                LOGGER.debug("Shutting down");
            } else if (msg.getMessage().equals(GUIMessageConstants.POINT_ON_MAP_CLICKED)) {
                LOGGER.debug("Sending new click info: " + msg.getData());
                networkSendQueue.offer(new ClientMessage(GUIMessageConstants.POINT_ON_MAP_CLICKED, msg.getData()));
            } else if (msg.getMessage().equals(GUIMessageConstants.GUI_INITIALIZED)) {
                guiReady.set(true);
            } else if (msg.getMessage().equals(GUIMessageConstants.GUI_INITIALIZED)) {
                gameGuiReady.set(true);
            }
        } catch (InterruptedException e) {
            LOGGER.error(ERROR_IN_CORE, e);
        }
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

    public BlockingQueue<ClientMessage> getNetworkSendQueue() {
        return networkSendQueue;
    }

    public void setNetworkSendQueue(BlockingQueue<ClientMessage> networkIncomingQueue) {
        this.networkSendQueue = networkIncomingQueue;
    }

    public BlockingQueue<ClientMessage> getNetworkReceiveQueue() {
        return networkReceiveQueue;
    }

    public void setNetworkReceiveQueue(BlockingQueue<ClientMessage> networkOutgoingQueue) {
        this.networkReceiveQueue = networkOutgoingQueue;
    }

}
