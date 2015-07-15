/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.server.core.game.states;

import au.com.ds.ef.EventEnum;

/**
 * Transitions of game states.
 * 
 * @author Josch Bosch
 */
public enum Events implements EventEnum {
    /**
     * Event type.
     */
    START_GAME_COMMAND,
    /**
     * Event type.
     */
    FINISHED_LOADING,
    /**
     * Event type.
     */
    GAME_ENDS,
    /**
     * Event type.
     */
    GAME_PAUSED
}
