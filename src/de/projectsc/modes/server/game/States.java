/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.server.game;

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
