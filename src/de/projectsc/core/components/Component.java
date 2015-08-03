/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.components;

import de.projectsc.core.entities.Entity;

public abstract class Component {

    protected String componentName;

    protected ComponentType type;

    public Component(String name) {
        this.componentName = name;
    }

    public abstract void update(Entity owner);

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
