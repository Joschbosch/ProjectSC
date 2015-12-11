/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component.impl.physic;

import java.io.File;
import java.util.Map;

import de.projectsc.core.component.ComponentType;

/**
 * When this component is attached to an entity, it will be able to collide with the world or being
 * able to be selected.
 *
 * It has different modules that represent different collision boxes.
 *
 * @author Josch Bosch
 */
public class ColliderComponent extends PhysicsComponent {

    /**
     * ID.
     */
    public static final String NAME = "Collider Component";

    public ColliderComponent() {
        setType(ComponentType.PREPHYSICS);
        setID(NAME);
    }

    @Override
    public void update(long ownerEntity) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isValidForSaving() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Map<String, Object> serialize(File savingLocation) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deserialize(Map<String, Object> serialized, File loadingLocation) {
        // TODO Auto-generated method stub

    }

}
