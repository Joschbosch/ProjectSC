/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.server.core.spi;

import de.projectsc.core.game.GameConfiguration;
import de.projectsc.modes.server.core.data.AuthenticatedClient;
import de.projectsc.modes.server.core.data.States;

/**
 * Interface for all games.
 * 
 * @author Josch Bosch
 */
public interface Game extends Runnable {

    /**
     * 
     * @return true, if the game is still alive.
     */
    boolean isAlive();

    /**
     * @return current number of player.
     */
    int getPlayerCount();

    /**
     * Shutdown game.
     */
    void shutdown();

    /**
     * @return id of the game
     */
    long getGameID();

    /**
     * @return true, if player can join game.
     */
    String isJoinable();

    /**
     * @param client new player for the game.
     */
    void addPlayerToGameLobby(AuthenticatedClient client);

    /**
     * 
     * @return current state of the game.
     */
    States getCurrentState();

    /**
     * @return display name for the game.
     */
    String getDisplayName();

    /**
     * @return configuration.
     */
    GameConfiguration getConfiguration();
}
