/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.server.core.messages;

/**
 * Constants in a {@link Game} object.
 * 
 * @author Josch Bosch
 */
public final class GameMessageConstants {

    /**
     * New host in lobby.
     */
    public static final String NEW_HOST = "new_host";

    public static final String START_GAME_REQUEST = "request:start_game";

    public static final String START_GAME = "response:start_game";

    public static final String ERROR_STARTING_GAME = "response:error_starting_game";

    private GameMessageConstants() {};
}
