/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.core.entities;

import org.lwjgl.util.vector.Vector3f;

/**
 * An entity that is only for decoration. This can be loaded from the beginning in the map file but
 * also be added when entities die or sth like it.
 * 
 * @author Josch Bosch
 */
public class DecorationEntity extends WorldEntity {

    public DecorationEntity(String model, String texture, Vector3f position, Vector3f rotation, float scale) {
        super(EntityType.DECORATION, model, texture, position, rotation, scale);
    }

    public DecorationEntity(Integer id, String model, String texture, Vector3f position, Vector3f rotation, float scale) {
        super(id, EntityType.DECORATION, model, texture, position, rotation, scale);
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
