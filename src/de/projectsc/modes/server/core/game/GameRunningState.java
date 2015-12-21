/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.server.core.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.messages.GameMessageConstants;
import de.projectsc.core.terrain.Terrain;
import de.projectsc.modes.server.core.game.data.ServerPlayer;
import de.projectsc.modes.server.core.messages.ServerMessage;

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

    private Terrain terrain;

    private long gameTick = 0;

    @Override
    public void call(GameContext context) throws Exception {
        LOGGER.debug("Entered game state " + context.getState());
        this.context = context;
        this.terrain = context.getTerrain();
        sendMessageToAllPlayers(new ServerMessage(GameMessageConstants.BEGIN_GAME));
        context.changeState(this);
    }

    @Override
    public void loop() {
        gameTick++;
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
        // Cloner cloner = new Cloner();

        // MyClass clone=cloner.deepClone(o);
    }

    private BufferedImage createWalkablePathImage() {
        if (terrain != null) {
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
                        LOGGER.debug("not walkable to set but not implemented");
                    } else {
                        pathG.setColor(Color.BLACK);
                    }
                    pathG.fillRect((int) (i * Terrain.TERRAIN_TILE_SIZE),
                        (int) (j * Terrain.TERRAIN_TILE_SIZE), (int) Terrain.TERRAIN_TILE_SIZE, (int) Terrain.TERRAIN_TILE_SIZE);
                }
            }
            return path;
        }
        return null;
    }

    private BufferedImage createCollisionTreeImage() {
        if (terrain != null) {
            BufferedImage tree =
                new BufferedImage((int) (terrain.getMapSize() * Terrain.TERRAIN_TILE_SIZE),
                    (int) (terrain.getMapSize() * Terrain.TERRAIN_TILE_SIZE),
                    BufferedImage.TYPE_INT_RGB);
            Graphics treeG = tree.getGraphics();
            treeG.setColor(Color.BLACK);
            treeG.fillRect(0, 0, tree.getWidth(), tree.getHeight());
            treeG.setColor(Color.RED);
            // OctTreeUtils.drawImage(treeG, collisionTree);
            return tree;
        }
        return null;
    }

    @Override
    public void handleMessage(ServerPlayer player, ServerMessage msg) {

    }

    @Override
    public void handleMessages(ServerPlayer player, List<ServerMessage> msg) {

    }
}
