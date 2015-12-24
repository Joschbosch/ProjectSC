/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.entities;

import de.projectsc.core.data.Event;

public class ChangeEntitySelectEvent extends Event {

    /**
     * ID.
     */
    public static final String ID = "ChangeEntitySelectEvent";

    private final boolean selected;

    private boolean hightLighted;

    public ChangeEntitySelectEvent(String entityId, boolean selected, boolean highligted) {
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
