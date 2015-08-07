/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.client.core.states;

import au.com.ds.ef.EventEnum;

/**
 * Transitions of game states.
 * 
 * @author Josch Bosch
 */
public enum ClientEvents implements EventEnum {
    /**
     * Event type.
     */
    ENTER_OR_CREATE_GAME,
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
