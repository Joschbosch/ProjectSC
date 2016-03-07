/*
 * Copyright (C) 2015
 */

package de.projectsc.core.entities.states;

/**
 * Represent a state of the entity.
 * 
 * @author Josch Bosch
 */
public enum EntityState {
    /**
     * State.
     */
    IDLING, MOVING, AUTO_ATTACKING, DYING, DEAD, MOVING_TO_BASIC_ATTACK, FOLLOWING_PATH, UNKNOWN
}
