/*
 * Copyright (C) 2015
 */

package de.projectsc.server.core;

import java.util.Map;
import java.util.TreeMap;

import au.com.ds.ef.StatefulContext;

public class GameContext extends StatefulContext {

    private static final long serialVersionUID = 7971613418627835197L;

    private ServerPlayer host;

    private final Map<Long, ServerPlayer> players;

    private final int gameID;

    private final String displayName;

    private final Game game;

    public GameContext(int id, String displayName, ServerPlayer host, Game game) {
        this.gameID = id;
        this.displayName = displayName;
        this.host = host;
        players = new TreeMap<>();
        players.put(this.host.getId(), this.host);
        this.game = game;
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

}
