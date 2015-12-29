/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.entity.component;

import de.projectsc.core.data.EntityEvent;
import de.projectsc.core.interfaces.Component;

/**
 * A component was added to an entity.
 * 
 * @author Josch Bosch
 */
public class ComponentAddedEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = ComponentAddedEvent.class.getName();

    private Component component;

    public ComponentAddedEvent(String entityID, Component c) {
        super(ID, entityID);
        this.component = c;
    }

    public Component getComponent() {
        return component;
    }
}
