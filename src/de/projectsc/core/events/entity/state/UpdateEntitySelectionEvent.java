/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.entity.state;

import de.projectsc.core.data.EntityEvent;

/**
 * Change the selection of an entity.
 * 
 * @author Josch Bosch
 */
public class UpdateEntitySelectionEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = UpdateEntitySelectionEvent.class.getName();

    private final boolean selected;

    private boolean hightLighted;

    public UpdateEntitySelectionEvent(String entityId, boolean selected, boolean highligted) {
        super(ID, entityId);
        this.selected = selected;
        this.hightLighted = highligted;
    }

    public boolean getSelected() {
        return selected;
    }

    public boolean isHightLighted() {
        return hightLighted;
    }

}
