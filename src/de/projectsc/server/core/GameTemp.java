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
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.Terrain;
import de.projectsc.core.TerrainLoader;
import de.projectsc.core.Tile;
import de.projectsc.core.entities.EntityType;
import de.projectsc.core.entities.MovingEntity;
import de.projectsc.core.entities.WorldEntity;
import de.projectsc.core.utils.OctTree;
import de.projectsc.server.core.gameutils.FutureEventQueue;

public class GameTemp {

    private static final int START_GAME_TIME = -30000;

    private static final String CORE_ERROR = "Core Error: ";

    private static final Log LOGGER = LogFactory.getLog(ServerCore.class);

    private static final long MS_PER_UPDATE = 50;

    private boolean shutdown;

    private final Map<Integer, WorldEntity> entities = new TreeMap<>();

    private long gameTime = 0;

    private final AtomicBoolean startGame = new AtomicBoolean(false);

    private FutureEventQueue futureQueue;

    private Terrain terrain;

    private Map<Integer, WorldEntity> staticEntities;

    private OctTree<WorldEntity> collisionTree;

    public GameTemp() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                createWorldEntities();
                List<WorldEntity> entitiesToSend = new LinkedList<WorldEntity>();
                for (WorldEntity e : entities.values()) {
                    if (e.getType() != EntityType.DECORATION && e.getType() != EntityType.SOLID_BACKGROUND_OBJECT) {
                        entitiesToSend.add(e);
                    }

                }
                // networkSendQueue.offer(new ServerMessage(NetworkMessageConstants.INITIALIZE_GAME,
                // entitiesToSend));
                while (!startGame.get() && !shutdown) {
                    try {
                        Thread.sleep(MS_PER_UPDATE);
                    } catch (InterruptedException e) {
                        LOGGER.error(CORE_ERROR, e);
                    }
                }
                gameTime = START_GAME_TIME;
                // futureQueue = new FutureEventQueue();
                // futureQueue.add(new FutureEvent(START_GAME_TIME + 10000, new
                // GameTimeUpdateTask()));
                // futureQueue.add(new FutureEvent(START_GAME_TIME + 10000, new UpdateTask()));
                // networkSendQueue.offer(new ServerMessage("Start game", gameTime));

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

                LOGGER.debug("Core shut down");
            }

        }).start();
    }

    private void moveGoats(long elapsedTime) {
        for (WorldEntity e : entities.values()) {
            if (e instanceof MovingEntity) {
                ((MovingEntity) e).move(elapsedTime);

            }
        }
        // collisionTree.update();
        for (WorldEntity e : entities.values()) {
            // if (collisionTree.getIntersectionList().contains(e)) {
            // ((MovingEntity) e).move(-elapsedTime);
            // } else {
            // networkSendQueue.offer(new ServerMessage(NetworkMessageConstants.NEW_LOCATION, new
            // float[] { e.getID(),
            // e.getPosition().x, e.getPosition().z, e.getRotY() }));
        }
        // }
    }

    private void createWorldEntities() {
        terrain = TerrainLoader.loadTerrain("newDataMap.psc");
        staticEntities = terrain.getStaticObjects();
        terrain.buildNeighborhood();
        terrain.makeStaticObjectsNotWalkable();
        // staticEntities.values();
        // List<Integer> remove = new LinkedList<>();
        // for (WorldEntity e : staticEntities.values()) {
        // if (e.getModel().equals("house")) {
        // remove.add(e.getID());
        // }
        // }
        // for (Integer i : remove) {
        // staticEntities.remove(i);
        // }
        // int xSize = 88;
        // int zSize = 60;
        // for (int i = 0; i < 10; i++) {
        // for (int j = 0; j < 10; j++) {
        // if (i == 0 || j == 0 || i == 9 || j == 9) {
        // WorldEntity worldEntity =
        // new WorldEntity(EntityType.SOLID_BACKGROUND_OBJECT, "house", "house.png", new Vector3f(i
        // * xSize + 100, 0, j * zSize + 100),
        // new Vector3f(
        // 0, 0.0f, 0), 10f);
        // staticEntities.put(worldEntity.getID(), worldEntity);
        // }
        // }
        // }
        // TerrainLoader.storeTerrain(terrain, "housingMap.psc");
        loadMovingEntities();
        collisionTree = new OctTree<WorldEntity>(terrain.getMapBoundingBox());
        for (WorldEntity entity : staticEntities.values()) {
            if (entity.getType() == EntityType.SOLID_BACKGROUND_OBJECT) {
                collisionTree.addEntity(entity);
            }
        }
        for (WorldEntity entity : entities.values()) {
            collisionTree.addEntity(entity);
            terrain.markEntityPosition(entity, Tile.NOT_WALKABLE);
        }
        collisionTree.recalculateTree();
    }

    private void loadMovingEntities() {
        // worldPlayer = new Player(new Vector3f(330f, 0f, 330f), new Vector3f(0, 0, 0), 1.4f);
        // entities.put(worldPlayer.getID(), worldPlayer);
        // for (int i = 0; i < 5; i++) {
        // WorldEntity worldEntity =
        // new MovingEntity("goat", "white.png", new Vector3f(340 + i * 10, 0, 340), new Vector3f(0,
        // 1.0f, 0), 7f);
        // entities.put(worldEntity.getID(), worldEntity);
        // }
    }

    private void workEventQueue() {
        // while (futureQueue.peek().getExecutionTime() < gameTime) {
        // FutureEvent event = futureQueue.remove();
        // if (event.getTask() instanceof GameTimeUpdateTask) {
        // networkSendQueue.offer(new ServerMessage(NetworkMessageConstants.GAME_TIME_UPDATE,
        // gameTime));
        // futureQueue.offer(new FutureEvent(event.getExecutionTime() + 10 * 10 * 10, new
        // GameTimeUpdateTask()));
        // } else if (event.getTask() instanceof UpdateTask) {
        // for (WorldEntity e : entities.values()) {
        // if (e.getType() == EntityType.MOVEABLE_OBJECT && !(e instanceof Player)) {
        // Vector3f position = e.getPosition();
        // float newX = (float) ((Math.random() * 2 * 2 * 10 * 10 - 2 * 10 * 10) + position.x);
        // float newZ = (float) ((Math.random() * 2 * 2 * 10 * 10 - 2 * 10 * 10) + position.z);
        // ((MovingEntity) e).setCurrentTarget(new Vector3f(newX, 0, newZ));
        // networkSendQueue.offer(new ServerMessage(NetworkMessageConstants.NEW_LOCATION, new int[]
        // { e.getID(),
        // (int) newX, (int) newZ }));
        // }
        // }
        // futureQueue.offer(new FutureEvent((long) (event.getExecutionTime() + Math.random() * 2 *
        // 10 * 10 * 10 + 3 * 10 * 10),
        // new UpdateTask()));
        // }
        // }
    }

}
