/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.core;

/**
 * 
 * Typed that entities can be.
 *
 * @author Josch Bosch
 */
public enum EntityType {
    /**
     * Solid objects that don't need to be moved.
     */
    SOLID_BACKGROUND_OBJECT,
    /**
     * Background objects.
     */
    BACKGROUND_OBJECT,
    /**
     * Interactive objects.
     */
    INTERACTIVE_OBJECT,
    /**
     * Moving Objects.
     */
    MOVEABLE_OBJECT,
    /**
     * A player.
     */
    PLAYER;
}
