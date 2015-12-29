/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.components;

import de.projectsc.core.data.EntityEvent;
import de.projectsc.core.interfaces.Component;

public class ComponentAddedEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = "Component Added Event";

    private Component component;

    public ComponentAddedEvent(String entityID, Component c) {
        super(ID, entityID);
        this.component = c;
    }

    public Component getComponent() {
        return component;
    }
}
