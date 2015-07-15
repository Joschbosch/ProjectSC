/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.server.core.game.states;

import au.com.ds.ef.StateEnum;

/**
 * The states for a Game.
 * 
 * @author Josch Bosch
 */
public enum States implements StateEnum {
    /**
     * State.
     */
    LOBBY,
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
