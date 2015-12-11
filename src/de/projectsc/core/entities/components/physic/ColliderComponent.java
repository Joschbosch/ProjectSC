/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.entities.components.physic;

import java.io.File;
import java.util.Map;

import de.projectsc.core.entities.ComponentType;

public class ColliderComponent extends PhysicsComponent {

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
