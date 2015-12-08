/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.entities;


/**
 * All game objects in the game are entities. All entities have components, that define their role and behavior.
 * 
 * @author Josch Bosch
 */
public class Entity {

    private static int idCount = 1000;

    private long entityTypeID;

    private final long entityUID;

    public Entity() {
        entityUID = idCount++;
    }

    public long getEntityTypeId() {
        return entityTypeID;
    }

    public long getID() {
        return entityUID;
    }

}
