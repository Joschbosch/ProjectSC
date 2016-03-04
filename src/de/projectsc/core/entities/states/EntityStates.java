/*
 * Copyright (C) 2015
 */

package de.projectsc.core.entities.states;

/**
 * Represent a state of the entity.
 * 
 * @author Josch Bosch
 */
public enum EntityStates {
    /**
     * State.
     */
    IDLING,
    /**
     * State.
     */
    MOVING,
    /**
     * State.
     */
    AUTO_ATTACKING,
    /**
     * State.
     */
    DYING,
    /**
     * State.
     */
    DEAD,
    MOVE_TO_BASIC_ATTACK,

    FOLLOWING_PATH,

    UNKNOWN
}
