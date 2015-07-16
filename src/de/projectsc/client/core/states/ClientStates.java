/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.core.states;

import au.com.ds.ef.StateEnum;

/**
 * The states for a Game.
 * 
 * @author Josch Bosch
 */
public enum ClientStates implements StateEnum {
    /**
     * State.
     */
    LOBBY,
    /**
     * State.
     */
    GAME_LOBBY,
    /**
     * State.
     */
    LOADING,
    /**
     * State.
     */
    RUNNING,
    /**
     * State.
     */
    FINISHED,
    /**
     * State.
     */
    PAUSED
}
