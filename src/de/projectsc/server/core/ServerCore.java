/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.server.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.core.ClientMessageConstants;
import de.projectsc.client.gui.GUIMessageConstants;
import de.projectsc.core.EntityType;
import de.projectsc.core.Player;
import de.projectsc.core.Terrain;
import de.projectsc.core.TerrainLoader;
import de.projectsc.core.WorldEntity;
import de.projectsc.core.data.messages.MessageConstants;
import de.projectsc.core.data.messages.NetworkMessageConstants;
import de.projectsc.server.core.tasks.GameTimeUpdateTask;
import de.projectsc.server.core.tasks.UpdateTask;

/**
 * Core of server.
 * 
 * @author Josch Bosch
 */
public class ServerCore implements Runnable {

    private static final int START_GAME_TIME = -30000;

    private static final String CORE_ERROR = "Core Error: ";

    private static final Log LOGGER = LogFactory.getLog(ServerCore.class);

    private static final long MS_PER_UPDATE = 50;

    private BlockingQueue<ServerMessage> networkSendQueue;

    private BlockingQueue<ServerMessage> networkReceiveQueue;

    private boolean shutdown;

    private final Map<Integer, WorldEntity> entities = new TreeMap<>();

    private Player worldPlayer;

    private long gameTime = 0;

    private final AtomicBoolean startGame = new AtomicBoolean(false);

    private FutureEventQueue futureQueue;

    private Terrain terrain;

    private Map<Integer, WorldEntity> staticEntities;

    public ServerCore() {
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
                        Thread.sleep(MS_PER_UPDATE);
                    } catch (InterruptedException e) {
                        LOGGER.error(CORE_ERROR, e);
                    }

                    workNetwork();
                }
            }
        }).start();

        new Thread(new Runnable() {

            @Override
            public void run() {
                createWorldEntities();
                List<WorldEntity> entitiesToSend = new LinkedList<WorldEntity>();
                for (WorldEntity e : entities.values()) {
                    if (e.getType() != EntityType.BACKGROUND_OBJECT && e.getType() != EntityType.SOLID_BACKGROUND_OBJECT) {
                        entitiesToSend.add(e);
                    }

                }
                networkSendQueue.offer(new ServerMessage(NetworkMessageConstants.INITIALIZE_GAME, entitiesToSend));
                while (!startGame.get()) {
                    try {
                        Thread.sleep(MS_PER_UPDATE);
                    } catch (InterruptedException e) {
                        LOGGER.error(CORE_ERROR, e);
                    }
                }
                gameTime = START_GAME_TIME;
                futureQueue = new FutureEventQueue();
                futureQueue.add(new FutureEvent(START_GAME_TIME + 10000, new GameTimeUpdateTask()));
                futureQueue.add(new FutureEvent(START_GAME_TIME + 10000, new UpdateTask()));
                networkSendQueue.offer(new ServerMessage("Start game", gameTime));

                long previous = System.currentTimeMillis();
                while (!shutdown) {
                    long current = System.currentTimeMillis();
                    long elapsed = current - previous;
                    previous = current;
                    gameTime += elapsed;
                    workEventQueue();
                    moveGoats(elapsed);
                    try {
                        Thread.sleep(MS_PER_UPDATE);
                    } catch (InterruptedException e) {
                        LOGGER.error(CORE_ERROR, e);
                    }
                }

                LOGGER.debug("Core initialized");
            }

        }).start();
    }

    private void workEventQueue() {
        while (futureQueue.peek().getExecutionTime() < gameTime) {
            FutureEvent event = futureQueue.remove();
            if (event.getTask() instanceof GameTimeUpdateTask) {
                networkSendQueue.offer(new ServerMessage(NetworkMessageConstants.GAME_TIME_UPDATE, gameTime));
                futureQueue.offer(new FutureEvent(event.getExecutionTime() + 10 * 10 * 10, new GameTimeUpdateTask()));
            } else if (event.getTask() instanceof UpdateTask) {
                for (WorldEntity e : entities.values()) {
                    if (e.getModel().equals("goat")) {
                        Vector3f position = e.getPosition();
                        float newX = (float) ((Math.random() * 2 * 2 * 10 * 10 - 2 * 10 * 10) + position.x);
                        float newZ = (float) ((Math.random() * 2 * 2 * 10 * 10 - 2 * 10 * 10) + position.z);
                        e.setCurrentTarget(new Vector3f(newX, 0, newZ));
                        networkSendQueue.offer(new ServerMessage(NetworkMessageConstants.NEW_LOCATION, new int[] { e.getID(),
                            (int) newX, (int) newZ }));
                    }
                }
                futureQueue.offer(new FutureEvent((long) (event.getExecutionTime() + Math.random() * 2 * 10 * 10 * 10 + 3 * 10 * 10),
                    new UpdateTask()));
            }
        }
    }

    private void moveGoats(long elapsedTime) {
        for (WorldEntity e : entities.values()) {
            if (e.getType() != EntityType.BACKGROUND_OBJECT && e.getType() != EntityType.SOLID_BACKGROUND_OBJECT) {
                e.move(elapsedTime);
                networkSendQueue.offer(new ServerMessage(NetworkMessageConstants.NEW_LOCATION, new float[] { e.getID(),
                    e.getPosition().x, e.getPosition().z, e.getRotY() }));
            }
        }
    }

    private void createWorldEntities() {
        terrain = TerrainLoader.loadTerrain("newMap.psc");
        staticEntities = terrain.getStaticObjects();
        staticEntities.values();
        // int xSize = 250;
        // int zSize = 310;
        // for (int i = 0; i < 5; i++) {
        // for (int j = 0; j < 5; j++) {
        // if (i == 0 || j == 0 || i == 4 || j == 4) {
        // WorldEntity worldEntity =
        // new WorldEntity(EntityType.SOLID_BACKGROUND_OBJECT, "house", "white.png", new Vector3f(i
        // * xSize, j * zSize, 0),
        // new Vector3f(
        // 0, 0.0f, 0), 0.5f);
        // staticEntities.put(worldEntity.getID(), worldEntity);
        // }
        // }
        // }
        // TerrainLoader.storeTerrain(terrain, "housingMap.psc");
        loadMovingEntities();

    }

    private void loadMovingEntities() {
        worldPlayer = new Player(new Vector3f(50f, 0f, 50f), new Vector3f(0, 0, 0), 1.4f);
        entities.put(worldPlayer.getID(), worldPlayer);
        for (int i = 0; i < 5; i++) {
            WorldEntity worldEntity =
                new WorldEntity(EntityType.MOVEABLE_OBJECT, "goat", "white.png", new Vector3f(55 + i * 10, 0, 50), new Vector3f(0,
                    1.0f, 0), 7f);
            entities.put(worldEntity.getID(), worldEntity);
        }
    }

    private void workNetwork() {
        while (!networkReceiveQueue.isEmpty()) {
            ServerMessage msg;
            try {
                msg = networkReceiveQueue.take();

                if (msg.getMessage().equals(MessageConstants.CLOSE_DOWN)) {
                    shutdown = true;
                    LOGGER.debug("Shutting down server core...");
                } else if (msg.getMessage().equals("ping")) {
                    LOGGER.debug("Got ping request with time " + (long) msg.getData());
                    networkSendQueue.offer(new ServerMessage("pong", new long[] { (long) msg.getData(), gameTime }));
                } else if (msg.getMessage().equals(GUIMessageConstants.POINT_ON_MAP_CLICKED)) {
                    worldPlayer.setCurrentTarget((Vector3f) msg.getData());
                } else if (msg.getMessage().equals(ClientMessageConstants.CLIENT_READY)) {
                    LOGGER.debug("Received client ready, starting game");
                    startGame.set(true);
                } else {
                    LOGGER.debug("Message not recognized: " + msg.getMessage());
                }
            } catch (InterruptedException e) {
                LOGGER.error("Error reading network messages: ", e);
            }
        }
    }

    public BlockingQueue<ServerMessage> getNetworkSendQueue() {
        return networkSendQueue;
    }

    public void setNetworkSendQueue(BlockingQueue<ServerMessage> networkSendQueue) {
        this.networkSendQueue = networkSendQueue;
    }

    public BlockingQueue<ServerMessage> getNetworkReceiveQueue() {
        return networkReceiveQueue;
    }

    public void setNetworkReceiveQueue(BlockingQueue<ServerMessage> networkReceiveQueue) {
        this.networkReceiveQueue = networkReceiveQueue;
    }
}
