/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */

package de.projectsc.core;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.objects.GraphicalEntity;

/**
 * Class for the player character, a special {@link GraphicalEntity}.
 * 
 * @author Josch Bosch
 */
public class Player extends WorldEntity {

    public Player(Vector3f position, Vector3f rotation, float scale) {
        super(EntityType.PLAYER, "person", "person.png", position, rotation, scale);
    }

}
