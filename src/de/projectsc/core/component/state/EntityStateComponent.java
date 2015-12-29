/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component.state;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.data.Scene;
import de.projectsc.core.entities.states.EntityState;

/**
 * Component that represents the state of an entity.
 * 
 * @author Josch Bosch
 */
public class EntityStateComponent extends DefaultComponent {

    /**
     * ID.
     */
    public static final String NAME = "Entity State Component";

    private EntityState state = EntityState.STANDING;

    private boolean moved = false;

    private boolean selected = false;

    private boolean selectAble = true;

    private boolean highlighted = false;

    private boolean highlightAble = true;

    public EntityStateComponent() {
        setType(ComponentType.PREPHYSICS);
        setComponentName(NAME);
    }

    @Override
    public void update(long elapsed) {

    }

    @Override
    public void addSceneInformation(Scene scene) {
        if (selectAble && selected) {
            scene.getSelectedEntites().add(owner.getID());
        }
        if (highlightAble && highlighted) {
            scene.getHightlightedEntites().add(owner.getID());
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
    public String serializeForNetwork() {
        int selectedInt = 0;
        if (isSelected()) {
            selectedInt = 1;
        }
        int highlightedInt = 0;
        if (isHighlighted()) {
            highlightedInt = 1;
        }
        return "" + state.ordinal() + ";" + selectedInt + ";" + highlightedInt;
    }

    @Override
    public void deserializeFromNetwork(String serialized) {
        String[] split = serialized.split(";");
        int ordinal = Integer.parseInt(split[0]);
        if (ordinal == EntityState.MOVING.ordinal()) {
            state = EntityState.MOVING;
        } else {
            state = EntityState.STANDING;
        }
        int selectedInt = Integer.parseInt(split[1]);
        int highlightedInt = Integer.parseInt(split[2]);
        selected = selectedInt != 0;
        highlighted = highlightedInt != 0;
    }

    @Override
    public boolean isValidForSaving() {
        return true;
    }

    /**
     * changes the state.
     * 
     * @param newState to change to
     */
    public void changeState(EntityState newState) {
        setState(newState);
    }

    public EntityState getState() {
        return state;
    }

    /**
     * @param newState to set
     * @return the new state.
     */
    public EntityState setState(EntityState newState) {
        this.state = newState;
        return newState;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public void setEntitySelected(boolean entitySelected) {
        this.selected = entitySelected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelectAble() {
        return selectAble;
    }

    public void setSelectAble(boolean selectAble) {
        this.selectAble = selectAble;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public boolean isHighlightAble() {
        return highlightAble;
    }

    public void setHighlightAble(boolean highlightAble) {
        this.highlightAble = highlightAble;
    }

}
