/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.core.components;

import de.projectsc.core.entities.Entity;

/**
 * 
 * Abstract class for entity components.
 * 
 * @author Josch Bosch
 */
public abstract class Component {

    protected String componentName;

    protected ComponentType type;

    public Component(String newName) {
        this.componentName = newName;
    }

    public abstract void update(Entity owner);

    public void receiveMessage(ComponentMessage message) {

    }

    public ComponentType getType() {
        return type;
    }

    public void setType(ComponentType type) {
        this.type = type;
    }

    public String getComponentName() {
        return componentName;
    }

}
