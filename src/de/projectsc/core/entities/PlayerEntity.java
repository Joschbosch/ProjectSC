/*
6 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.core.entities;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.client.gui.objects.GraphicalEntity;

/**
 * Class for the player character, a special {@link GraphicalEntity}.
 * 
 * @author Josch Bosch
 */
public class PlayerEntity extends MovingEntity {

    public PlayerEntity(Vector3f position, Vector3f rotation, float scale) {
        super("person", "person.png", position, rotation, scale);
        this.type = EntityType.PLAYER;
    }

    public PlayerEntity(Integer id, String model, String texture, Vector3f position, Vector3f rotation, float scale) {
        super(id, "person", "person.png", position, rotation, scale);
        this.type = EntityType.PLAYER;
    }

}
