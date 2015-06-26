/*
 * Copyright (C) 2015 
 */

package de.projectsc.server.core;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.core.ClientMessageConstants;
import de.projectsc.client.gui.GUIMessageConstants;
import de.projectsc.client.gui.terrain.Terrain;
import de.projectsc.core.EntityType;
import de.projectsc.core.Player;
import de.projectsc.core.WorldEntity;
import de.projectsc.core.data.messages.MessageConstants;
import de.projectsc.core.data.messages.NetworkMessageConstants;

public class ServerCore implements Runnable {

    private static final int SLEEP_TIME = 50;

    private static final Log LOGGER = LogFactory.getLog(ServerCore.class);

    protected static final long MS_PER_UPDATE = 50;

    private BlockingQueue<ServerMessage> networkSendQueue;

    private BlockingQueue<ServerMessage> networkReceiveQueue;

    private boolean shutdown;

    private List<WorldEntity> entities = new LinkedList<>();

    private Player worldPlayer;

    private long lastUpdate = 0;

    private long gameTime = 0;

    private AtomicBoolean startGame = new AtomicBoolean(false);

    private FutureEventQueue futureQueue;

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
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        LOGGER.error("Core Error: ", e);
                    }

                    workNetwork();
                }
            }
        }).start();

        new Thread(new Runnable() {

            @Override
            public void run() {
                createWorldEntities();
                while (!startGame.get()) {
                    try {
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        LOGGER.error("Core Error: ", e);
                    }
                }
                networkSendQueue.offer(new ServerMessage(NetworkMessageConstants.INITIALIZE_GAME, entities));
                gameTime = -30000;
                futureQueue = new FutureEventQueue();
                futureQueue.add(new FutureEvent(-20000, new GameTimeUpdateTask()));
                futureQueue.add(new FutureEvent(-20000, new UpdateTask()));
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
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        LOGGER.error("Core Error: ", e);
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
                futureQueue.offer(new FutureEvent(event.getExecutionTime() + 1000, new GameTimeUpdateTask()));
            } else if (event.getTask() instanceof UpdateTask) {
                for (WorldEntity e : entities) {
                    if (e.getModel().equals("goat")) {
                        Vector3f position = e.getPosition();
                        float newX = (float) ((Math.random() * 400 - 200) + position.x);
                        float newZ = (float) ((Math.random() * 400 - 200) + position.z);
                        e.setCurrentTarget(new Vector3f(newX, 0, newZ));
                        networkSendQueue.offer(new ServerMessage(NetworkMessageConstants.NEW_LOCATION, new int[] { e.getID(),
                            (int) newX, (int) newZ }));
                    }
                }
                futureQueue.offer(new FutureEvent((long) (event.getExecutionTime() + Math.random() * 2000 + 300), new UpdateTask()));
            }
        }
    }

    private void moveGoats(long elapsedTime) {
        for (WorldEntity e : entities) {
            if (e.getType() != EntityType.BACKGROUND_OBJECT && e.getType() != EntityType.SOLID_BACKGROUND_OBJECT) {
                e.move(elapsedTime);
                // if (e.getBoundingBox() != null && CollisionDetection.intersects(e, entities)) {
                // e.move(-elapsedTime);
                // }
                networkSendQueue.offer(new ServerMessage(NetworkMessageConstants.NEW_LOCATION, new float[] { e.getID(),
                    e.getPosition().x, e.getPosition().z, e.getRotY() }));
            }
        }
    }

    private void createWorldEntities() {
        loadStaticMapObject(100, EntityType.BACKGROUND_OBJECT, "terrain/fern", "terrain/fernTextureAtlas.png", 1f);
        loadStaticMapObject(100, EntityType.BACKGROUND_OBJECT, "terrain/grassModel", "terrain/grassTexture.png", 1f);
        loadStaticMapObject(50, EntityType.SOLID_BACKGROUND_OBJECT, "terrain/tree", "terrain/tree.png", 15f);
        loadStaticMapObject(50, EntityType.SOLID_BACKGROUND_OBJECT, "terrain/lowPolyTree", "terrain/lowPolyTree.png", 2f);
        loadStaticMapObject(100, EntityType.BACKGROUND_OBJECT, "terrain/fern", "terrain/flower.png", 1f);
        loadMovingEntities();

    }

    private void loadMovingEntities() {
        worldPlayer = new Player(new Vector3f(1f, 0f, -50f), 0, 0, 0, 1.4f);
        entities.add(worldPlayer);
        for (int i = 0; i < 5; i++) {
            entities.add(new WorldEntity(EntityType.MOVEABLE_OBJECT, "goat", "white.png", new Vector3f(-5 + i * 10, 0, -5), 0, 0, 0, 7f));
        }
    }

    private void loadStaticMapObject(int count, EntityType type, String model, String texture, float scale) {
        for (int i = 0; i < count; i++) {
            float randomX = (float) (Math.random() * Terrain.SIZE - Terrain.SIZE / 2);
            float randomZ = (float) (Math.random() * Terrain.SIZE - Terrain.SIZE / 2);
            entities.add(new WorldEntity(type, model, texture, new Vector3f(
                randomX, 0, randomZ), 0, 0, 0, scale));
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
