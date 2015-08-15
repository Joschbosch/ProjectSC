/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.server.core.game;

import java.util.Map;
import java.util.TreeMap;

import org.lwjgl.util.vector.Vector3f;

import au.com.ds.ef.StatefulContext;
import de.projectsc.core.Terrain;
import de.projectsc.core.TerrainLoader;
import de.projectsc.core.Tile;
import de.projectsc.core.components.impl.BoundingComponent;
import de.projectsc.core.entities.Entity;
import de.projectsc.core.game.GameAttributes;
import de.projectsc.core.game.GameConfiguration;
import de.projectsc.core.utils.OctTree;
import de.projectsc.server.core.ServerPlayer;

/**
 * Context for all game states.
 * 
 * @author Josch Bosch
 */
public class GameContext extends StatefulContext {

    private static final long serialVersionUID = 7971613418627835197L;

    private ServerPlayer host;

    private final Map<Long, ServerPlayer> players;

    private final int gameID;

    private final String displayName;

    private final Game game;

    private final GameConfiguration config;

    private boolean loading = false;

    private byte loadingProgress = 0;

    private Terrain terrain;

    private Map<Integer, Entity> staticEntities;

    private OctTree<Entity> collisionTree;

    private Map<Long, Entity> entities;

    public GameContext(int id, String displayName, ServerPlayer host, Game game) {
        this.gameID = id;
        this.displayName = displayName;
        this.host = host;
        players = new TreeMap<>();
        players.put(this.host.getId(), this.host);
        this.game = game;
        this.config = new GameConfiguration();
        this.config.setMapName("newDataMap");
        this.config.setPlayerCharacter(this.host.getId(), "person");
        this.config.setPlayerAffiliation(this.getHost().getId(), GameAttributes.AFFILIATION_LIGHT);
    }

    /**
     * Loads all neccesary data to the context.
     */
    public void loadData() {
        loading = true;
        entities = new TreeMap<>();

        terrain = TerrainLoader.loadTerrain(config.getMapName() + ".psc");
        loadingProgress = 50;

        terrain.buildNeighborhood();
        loadingProgress = 60;

        terrain.makeStaticObjectsNotWalkable();
        loadingProgress = 70;

        loadPlayerAndBots();

        collisionTree = new OctTree<>(terrain.getMapBoundingBox());
        for (Entity entity : staticEntities.values()) {
            if (entity.hasComponent(BoundingComponent.class)) {
                collisionTree.addEntity(entity);
            }
        }
        for (Entity entity : entities.values()) {
            collisionTree.addEntity(entity);
            terrain.markEntityPosition(entity, Tile.NOT_WALKABLE);
        }
        collisionTree.recalculateTree();
        loadingProgress = 100;
    }

    private void loadPlayerAndBots() {
        int affiliationLight = 0;
        int affiliationDark = 0;
        // fake values;
        Vector3f[] lightStarters =
            new Vector3f[] { new Vector3f(330f, 0f, 330f), new Vector3f(330f, 0f, 340f), new Vector3f(330f, 0f, 350f),
                new Vector3f(330f, 0f, 360f), new Vector3f(330f, 0f, 370f) }; // get positions from
        // fake values; // terrain
        Vector3f[] darkStarters =
            new Vector3f[] { new Vector3f(530f, 0f, 330f), new Vector3f(530f, 0f, 340f), new Vector3f(530f, 0f, 350f),
                new Vector3f(530f, 0f, 360f), new Vector3f(530f, 0f, 370f) }; // get positions from
                                                                              // terrain

        for (ServerPlayer p : players.values()) {
            byte affiliation = config.getPlayerAffiliation(p.getId());
            Vector3f startPosition = null;
            Vector3f startRotation = null;
            if (affiliation == GameAttributes.AFFILIATION_DARK) {
                startPosition = darkStarters[affiliationDark++];
                startRotation = new Vector3f(0f, 90f, 9f);
            } else {
                startPosition = lightStarters[affiliationLight++];
                startRotation = new Vector3f(0f, 270f, 9f);
            }
        }

    }

    public ServerPlayer getHost() {
        return host;
    }

    public Map<Long, ServerPlayer> getPlayers() {
        return players;
    }

    public int getGameID() {
        return gameID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setHost(ServerPlayer serverPlayer) {
        this.host = serverPlayer;
    }

    public Game getGame() {
        return game;
    }

    /**
     * Terminate context.
     */
    public void terminate() {
        setTerminated();
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public byte getLoadingProgress() {
        return loadingProgress;
    }

    public void setLoadingProgress(byte loadingProgress) {
        this.loadingProgress = loadingProgress;
    }

    public GameConfiguration getConfig() {
        return config;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public Map<Integer, Entity> getStaticEntities() {
        return staticEntities;
    }

    public OctTree<Entity> getCollisionTree() {
        return collisionTree;
    }

    public Map<Long, Entity> getEntities() {
        return entities;
    }

}
