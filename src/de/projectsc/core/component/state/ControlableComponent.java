/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.component.state;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.interfaces.Component;

/**
 * Component that flags, if an entity can be controlled by the player.
 * 
 * @author Josch Bosch
 */
public class ControlableComponent extends DefaultComponent {

    /**
     * ID.
     */
    public static final String NAME = "Controlable Component";

    public ControlableComponent() {
        setType(ComponentType.INPUT);
        setComponentName(NAME);
    }

    @Override
    public Component cloneComponent() {
        return new ControlableComponent();
    }

    @Override
    public boolean isValidForEntitySaving() {
        return true;
    }
}
