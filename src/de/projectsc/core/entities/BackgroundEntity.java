/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.core.entities;

import org.lwjgl.util.vector.Vector3f;

/**
 * This is an entity which does not move but is solid and cant be passed.
 * 
 * @author Josch Bosch
 */
public class BackgroundEntity extends WorldEntity {

    public BackgroundEntity(String model, String texture, Vector3f position, Vector3f rotation, float scale) {
        super(EntityType.SOLID_BACKGROUND_OBJECT, model, texture, position, rotation, scale);
    }

    public BackgroundEntity(int id, String model, String texture, Vector3f position, Vector3f rotation, float scale) {
        super(id, EntityType.SOLID_BACKGROUND_OBJECT, model, texture, position, rotation, scale);
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
