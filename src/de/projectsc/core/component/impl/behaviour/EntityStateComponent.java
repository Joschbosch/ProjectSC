/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component.impl.behaviour;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.entities.states.EntityState;
import de.projectsc.core.component.ComponentType;

public class EntityStateComponent extends DefaultComponent {

    public static final String NAME = "Entity State Component";

    private EntityState state = EntityState.STANDING;

    private boolean moved = false;

    public EntityStateComponent() {
        setType(ComponentType.PREPHYSICS);
        setID(NAME);
    }

    @Override
    public void update(long ownerEntity) {
        if (state == EntityState.MOVING) {
            setMoved(true);
        } else {
            setMoved(false);
        }
    }

    @Override
    public Map<String, Object> serialize(File savingLocation) {
        Map<String, Object> serialized = new HashMap<String, Object>();
        return serialized;
    }

    @Override
    public void deserialize(Map<String, Object> input, File loadingLocation) {

    }

    @Override
    public boolean isValidForSaving() {
        return true;
    }

    public void changeState(EntityState newState) {
        setState(newState);
    }

    public EntityState getState() {
        return state;
    }

    public EntityState setState(EntityState state) {
        this.state = state;
        return state;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

}