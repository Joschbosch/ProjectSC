/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.messages;

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

    /**
     * Message ID.
     */
    public static final String START_GAME_REQUEST = "request:start_game";

    /**
     * Message ID.
     */
    public static final String START_GAME = "response:start_game";

    /**
     * Message ID.
     */
    public static final String ERROR_STARTING_GAME = "response:error_starting_game";

    /**
     * Message ID.
     */
    public static final String UPDATE_LOADING_PROGRESS = "update_loading_progress";

    /**
     * Message ID.
     */
    public static final String BEGIN_GAME = "begin_game";

    /**
     * Message ID.
     */
    public static final String NEW_SNAPSHOT = "new_snapshot";

    private GameMessageConstants() {};
}
