/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.core.entities;

/**
 * 
 * Typed that entities can be.
 *
 * @author Josch Bosch
 */
public enum EntityType {
    /**
     * Background objects.
     */
    DECORATION,
    /**
     * Entity type for particels or other effects.
     */
    EFFECT,
    /**
     * Type for collectable items which are not solid.
     */
    COLLECTABLE,

    /**
     * Solid objects that don't need to be moved.
     */
    SOLID_BACKGROUND_OBJECT,
    /**
     * Interactive objects.
     */
    USABLE_OBJECT,
    /**
     * Moving Objects.
     */
    MOVEABLE_OBJECT,
    /**
     * A player.
     */
    PLAYER;
}
