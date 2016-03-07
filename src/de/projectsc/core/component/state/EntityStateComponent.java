/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component.state;

import de.projectsc.core.CoreConstants;
import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.entities.states.EntityState;
import de.projectsc.core.interfaces.Component;

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

    private EntityState state = EntityState.IDLING;

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
    public String serializeForNetwork() {
        int selectedInt = 0;
        if (isSelected()) {
            selectedInt = 1;
        }
        int highlightedInt = 0;
        if (isHighlighted()) {
            highlightedInt = 1;
        }
        return "" + state.ordinal() + CoreConstants.SERIALIZATION_SEPARATOR
            + selectedInt + CoreConstants.SERIALIZATION_SEPARATOR + highlightedInt;
    }

    @Override
    public void deserializeFromNetwork(String serialized) {
        String[] split = serialized.split(CoreConstants.SERIALIZATION_SEPARATOR);
        int ordinal = Integer.parseInt(split[0]);
        EntityState[] values = EntityState.values();
        if (ordinal >= values.length) {
            state = EntityState.UNKNOWN;
        } else {
            state = values[ordinal];
        }
        int selectedInt = Integer.parseInt(split[1]);
        int highlightedInt = Integer.parseInt(split[2]);
        selected = selectedInt != 0;
        highlighted = highlightedInt != 0;
    }

    @Override
    public Component cloneComponent() {
        EntityStateComponent esc = new EntityStateComponent();
        esc.setEntitySelected(selected);
        esc.setHighlightAble(highlightAble);
        esc.setHighlighted(highlighted);
        esc.setSelectAble(selectAble);
        esc.setMoved(moved);
        esc.setState(state);
        return esc;
    }

    @Override
    public boolean isValidForEntitySaving() {
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

    /**
     * @return has entity moved?
     */
    public boolean hasMoved() {
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
