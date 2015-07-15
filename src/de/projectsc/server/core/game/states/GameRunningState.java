/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.server.core.game.states;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rits.cloning.Cloner;

import de.projectsc.core.Terrain;
import de.projectsc.core.Tile;
import de.projectsc.core.entities.PlayerEntity;
import de.projectsc.core.entities.WorldEntity;
import de.projectsc.core.utils.OctTree;
import de.projectsc.server.core.ServerPlayer;
import de.projectsc.server.core.game.GameContext;
import de.projectsc.server.core.messages.GameMessageConstants;
import de.projectsc.server.core.messages.ServerMessage;

/**
 * State when the game is started and running. This is the main state for all the game logic.
 * 
 * @author Josch Bosch
 */
public class GameRunningState extends GameState {

    /**
     * Constant.
     */
    public static final long GAME_TICK_TIME = 16;

    private static final Log LOGGER = LogFactory.getLog(LoadingState.class);

    private OctTree<WorldEntity> collisionTree;

    private Terrain terrain;

    private Map<Integer, WorldEntity> staticEntities;

    private Map<Integer, WorldEntity> entities;

    private long gameTick = 0;

    private long time = 0;

    @Override
    public void call(GameContext context) throws Exception {
        LOGGER.debug("Entered game state " + context.getState());
        this.context = context;
        this.collisionTree = context.getCollisionTree();
        this.terrain = context.getTerrain();
        this.entities = context.getEntities();
        this.staticEntities = context.getStaticEntities();
        sendMessageToAllPlayers(new ServerMessage(GameMessageConstants.BEGIN_GAME));
        time = System.currentTimeMillis();
        context.getGame().changeState(this);
    }

    @Override
    public void loop() {
        gameTick++;
        time = System.currentTimeMillis();
        PlayerEntity e = context.getPlayers().get(0L).getEntity();
        terrain.markEntityPosition(e, Tile.WALKABLE);
        e.move(GAME_TICK_TIME);
        terrain.markEntityPosition(e, Tile.NOT_WALKABLE);
        collisionTree.update();
        if (gameTick % 5 == 0) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    BufferedImage path = createWalkablePathImage();
                    BufferedImage tree = createCollisionTreeImage();
                    sendMessageToPlayer(context.getPlayers().get(0L), new ServerMessage("newImage", tree, path));
                }

            }).start();
        }
        createSnapshot();
    }

    private void createSnapshot() {
        Cloner cloner = new Cloner();

        // MyClass clone=cloner.deepClone(o);
    }

    private BufferedImage createWalkablePathImage() {
        BufferedImage path =
            new BufferedImage((int) (terrain.getMapSize() * Terrain.TERRAIN_TILE_SIZE),
                (int) (terrain.getMapSize() * Terrain.TERRAIN_TILE_SIZE),
                BufferedImage.TYPE_INT_RGB);
        Graphics pathG = path.getGraphics();
        pathG.setColor(Color.BLACK);
        pathG.fillRect(0, 0, path.getWidth(), path.getHeight());
        pathG.setColor(Color.RED);
        for (int i = 0; i < terrain.getMapSize(); i++) {
            for (int j = 0; j < terrain.getMapSize(); j++) {
                if (terrain.getTerrain()[i][j] != null) {
                    if (terrain.getTerrain()[i][j].getWalkAble() == Tile.WALKABLE) {
                        pathG.setColor(Color.GREEN);
                    } else if (terrain.getTerrain()[i][j].getWalkAble() == Tile.LATER_WALKABLE) {
                        pathG.setColor(Color.CYAN);
                    } else {
                        pathG.setColor(Color.RED);
                    }
                } else {
                    pathG.setColor(Color.BLACK);
                }
                pathG.fillRect((int) (i * Terrain.TERRAIN_TILE_SIZE),
                    (int) (j * Terrain.TERRAIN_TILE_SIZE), (int) Terrain.TERRAIN_TILE_SIZE, (int) Terrain.TERRAIN_TILE_SIZE);
            }
        }
        return path;
    }

    private BufferedImage createCollisionTreeImage() {
        BufferedImage tree =
            new BufferedImage((int) (terrain.getMapSize() * Terrain.TERRAIN_TILE_SIZE),
                (int) (terrain.getMapSize() * Terrain.TERRAIN_TILE_SIZE),
                BufferedImage.TYPE_INT_RGB);
        Graphics treeG = tree.getGraphics();
        treeG.setColor(Color.BLACK);
        treeG.fillRect(0, 0, tree.getWidth(), tree.getHeight());
        treeG.setColor(Color.RED);
        collisionTree.drawImage(treeG);
        return tree;
    }

    @Override
    public void handleMessage(ServerPlayer player, ServerMessage msg) {

    }

    @Override
    public void handleMessages(ServerPlayer player, List<ServerMessage> msg) {

    }
}
