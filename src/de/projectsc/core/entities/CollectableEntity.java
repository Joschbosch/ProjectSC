/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.entities;

import org.lwjgl.util.vector.Vector3f;

/**
 * This represents an entity which is collectible in the world, so it has a bounding box but is not
 * solid (item can not be collected by enemy...).
 * 
 * @author Josch Bosch
 */
public class CollectableEntity extends WorldEntity {

    public CollectableEntity(String model, String texture, Vector3f position, Vector3f rotation, float scale) {
        super(EntityType.COLLECTABLE, model, texture, position, rotation, scale);
    }

    @Override
    public boolean isMovable() {
        return false;
    }

    @Override
    public boolean hasMoved() {
        return false;
    }

}
