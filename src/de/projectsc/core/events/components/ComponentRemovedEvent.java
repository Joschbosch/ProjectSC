/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.components;

import de.projectsc.core.data.Event;
import de.projectsc.core.interfaces.Component;

public class ComponentRemovedEvent extends Event {

    /**
     * ID.
     */
    public static final String ID = "Component Removed Event";

    private Component component;

    public ComponentRemovedEvent(String entityID, Component c) {
        super(ID, entityID);
        this.component = c;
    }

    public Component getComponent() {
        return component;
    }
}
