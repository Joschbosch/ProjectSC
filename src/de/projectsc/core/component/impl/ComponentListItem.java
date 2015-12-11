/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component.impl;

import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.component.impl.behaviour.EntityStateComponent;
import de.projectsc.core.component.impl.physic.MeshComponent;
import de.projectsc.core.component.impl.physic.VelocityComponent;

public enum ComponentListItem {
    VELOCITY_COMPONENT(VelocityComponent.NAME, VelocityComponent.class),
    ENTITY_STATE_COMPONENT(EntityStateComponent.NAME, EntityStateComponent.class),
    MESH_COMPONENT(MeshComponent.NAME, MeshComponent.class);

    private String name;

    private Class<? extends DefaultComponent> clazz;

    ComponentListItem(String name, Class<? extends DefaultComponent> clazz) {
        this.setName(name);
        this.setClazz(clazz);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<? extends DefaultComponent> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends DefaultComponent> clazz) {
        this.clazz = clazz;
    }

}
