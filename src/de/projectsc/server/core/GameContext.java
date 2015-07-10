/*
 * Copyright (C) 2015
 */

package de.projectsc.server.core;

import java.util.Map;
import java.util.TreeMap;

import org.lwjgl.util.vector.Vector3f;

import au.com.ds.ef.StatefulContext;
import de.projectsc.core.Terrain;
import de.projectsc.core.TerrainLoader;
import de.projectsc.core.Tile;
import de.projectsc.core.entities.EntityType;
import de.projectsc.core.entities.PlayerEntity;
import de.projectsc.core.entities.WorldEntity;
import de.projectsc.core.game.GameAttributes;
import de.projectsc.core.utils.OctTree;

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

    private Map<Integer, WorldEntity> staticEntities;

    private OctTree<WorldEntity> collisionTree;

    private Map<Integer, WorldEntity> entities;

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

    public void loadData() {
        loading = true;
        entities = new TreeMap<Integer, WorldEntity>();

        terrain = TerrainLoader.loadTerrain(config.getMapName() + ".psc");
        staticEntities = terrain.getStaticObjects();
        loadingProgress = 50;

        terrain.buildNeighborhood();
        loadingProgress = 60;

        terrain.makeStaticObjectsNotWalkable();
        loadingProgress = 70;

        loadPlayerAndBots();

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
        loadingProgress = 100;
    }

    private void loadPlayerAndBots() {
        int affiliationLight = 0;
        int affiliationDark = 0;
        Vector3f[] lightStarters =
            new Vector3f[] { new Vector3f(330f, 0f, 330f), new Vector3f(330f, 0f, 340f), new Vector3f(330f, 0f, 350f),
                new Vector3f(330f, 0f, 360f), new Vector3f(330f, 0f, 370f) }; // get positions from terrain
        Vector3f[] darkStarters =
            new Vector3f[] { new Vector3f(530f, 0f, 330f), new Vector3f(530f, 0f, 340f), new Vector3f(530f, 0f, 350f),
                new Vector3f(530f, 0f, 360f), new Vector3f(530f, 0f, 370f) }; // get positions from terrain

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
            PlayerEntity entity = new PlayerEntity(startPosition, startRotation, 1.0f);
            p.setWorldEntity(entity);
            entities.put(entity.getID(), entity);
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

    public Map<Integer, WorldEntity> getStaticEntities() {
        return staticEntities;
    }

    public OctTree<WorldEntity> getCollisionTree() {
        return collisionTree;
    }

    public Map<Integer, WorldEntity> getEntities() {
        return entities;
    }

}
