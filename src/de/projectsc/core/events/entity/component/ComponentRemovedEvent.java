/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.entity.component;

import de.projectsc.core.data.EntityEvent;
import de.projectsc.core.interfaces.Component;

/**
 * A component was removed from an entity.
 * 
 * @author Josch Bosch
 */
public class ComponentRemovedEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = ComponentRemovedEvent.class.getName();

    private Component component;

    public ComponentRemovedEvent(String entityID, Component c) {
        super(ID, entityID);
        this.component = c;
    }

    public Component getComponent() {
        return component;
    }
}
