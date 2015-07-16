/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.core.data.messages;

/**
 * All constants for messages that are not game related.
 *
 * @author Josch Bosch
 */
public final class MessageConstants {

    /**
     * Constant.
     */
    public static final String SHUTDOWN = "shutdown";

    /**
     * Constant.
     */
    public static final String CONNECT = "request:client_connect"; // data for login: ip, port

    /**
     * Constant.
     */
    public static final String DISCONNECT = "request:client_disconnect"; //

    /**
     * Constant.
     */
    public static final String SERVER_WELCOME = "response:welcome"; //

    /**
     * Constant.
     */
    public static final String CLIENT_LOGIN_REQUEST = "request:login"; // Login data

    /**
     * Constant.
     */
    public static final String LOGIN_SUCCESSFUL = "response:login_successful"; // List of clients in lobby and games

    /**
     * Constant.
     */
    public static final String LOGIN_FAILED = "response:login_failed"; // Failure message

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
    public static final String PLAYER_QUIT_LOBBY_REQUEST = "request:player_quit_lobby";

    /**
     * Constant.
     */
    public static final String CLIENT_JOINED_LOBBY = "client_joined_lobby";

    /**
     * Constant.
     */
    public static final String CLIENT_LEFT_LOBBY = "client_left_lobby";

    /**
     * Constant.
     */
    public static final String CREATE_NEW_GAME_REQUEST = "request:create_new_game";

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

    private MessageConstants() {};
}
