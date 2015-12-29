/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.server.game;

import java.util.Map;
import java.util.TreeMap;

import de.projectsc.core.game.GameAttributes;
import de.projectsc.core.game.GameConfiguration;
import de.projectsc.core.interfaces.Entity;
import de.projectsc.modes.server.game.data.ServerPlayer;

/**
 * Context for all game states.
 * 
 * @author Josch Bosch
 */
public class GameContext {

    private static final long serialVersionUID = 7971613418627835197L;

    private ServerPlayer host;

    private final Map<String, ServerPlayer> players;

    private final int gameID;

    private final String displayName;

    private final GameConfiguration config;

    private boolean loading = false;

    private byte loadingProgress = 0;

    private Map<Integer, Entity> staticEntities;

    private Map<Long, Entity> entities;

    private final Game game;

    public GameContext(int id, String displayName, ServerPlayer host, Game game) {
        this.gameID = id;
        this.displayName = displayName;
        this.host = host;
        this.game = game;
        players = new TreeMap<>();
        players.put(this.host.getId(), this.host);
        this.config = new GameConfiguration();
        this.config.setMapName("L1/first.map");
        this.config.setPlayerCharacter(this.host.getId(), "person");
        this.config.setPlayerAffiliation(this.getHost().getId(), GameAttributes.AFFILIATION_LIGHT);
    }

    /**
     * Loads all neccesary data to the context.
     */
    public void loadData() {
        loading = true;
        entities = new TreeMap<>();

        loadMapAndEntites();
        loadingProgress = 50;
        loadingProgress = 60;
        loadingProgress = 70;

        loadPlayerAndBots();
        loadingProgress = 100;
    }

    private void loadMapAndEntites() {

    }

    private void loadPlayerAndBots() {

    }

    public ServerPlayer getHost() {
        return host;
    }

    public Map<String, ServerPlayer> getPlayers() {
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

    /**
     * Terminate context.
     */
    public void terminate() {}

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

    public Map<Integer, Entity> getStaticEntities() {
        return staticEntities;
    }

    public Map<Long, Entity> getEntities() {
        return entities;
    }

}
