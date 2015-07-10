/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.server.core.messages;

/**
 * All constants in a server.
 *
 * @author Josch Bosch
 */
public final class ServerMessageConstants {

    /**
     * Constant.
     */
    public static final String CHAT_MESSAGE = "client_chat_msg";

    /**
     * Constant.
     */
    public static final String NEW_CLIENT_CONNECTED = "new_client_connect";

    /**
     * Constant.
     */
    public static final String CLIENT_DISCONNECTED = "client_disconnected";

    /**
     * Constant.
     */
    public static final String PLAYER_QUIT_LOBBY = "request:player_quit_lobby";

    /**
     * Constant.
     */
    public static final String CLIENT_JOINED_LOBBY = "client_joined_lobby";

    /**
     * Constant.
     */
    public static final String CREATE_NEW_GAME_REQUEST = "request:create_new_game";

    /**
     * Constant.
     */
    public static final String CLIENT_LEFT_LOBBY = "client_left_lobby";

    /**
     * Constant.
     */
    public static final String JOIN_GAME_REQUEST = "request:join_game";

    /**
     * Constant.
     */
    public static final String ERROR_JOINING_GAME = "response:error_joining_game";

    /**
     * Constant.
     */
    public static final String JOIN_GAME_SUCCSESSFULL = "response:join_game_successfull";

    /**
     * Constant.
     */
    public static final String JOIN_LOBBY = "response:join_main_lobby";

    /**
     * Constant.
     */
    public static final String NEW_GAME_CREATED = "response:new_game_created";

    /**
     * Constant.
     */
    public static final String PLAYER_JOINED_GAME = "player_joined_game";

    private ServerMessageConstants() {};
}
