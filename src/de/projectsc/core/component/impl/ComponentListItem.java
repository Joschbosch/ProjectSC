/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component.impl;

import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.component.impl.behaviour.EntityStateComponent;
import de.projectsc.core.component.impl.physic.ColliderComponent;
import de.projectsc.core.component.impl.physic.MeshComponent;
import de.projectsc.core.component.impl.physic.TransformComponent;
import de.projectsc.core.component.impl.physic.VelocityComponent;

/**
 * These are all (non GUI) components that are known in the engine.
 *
 * @author Josch Bosch
 */
public enum ComponentListItem {
    /**
     * Component for movement.
     */
    VELOCITY_COMPONENT(VelocityComponent.NAME, VelocityComponent.class),
    /**
     * Represents the state of an entity.
     */
    ENTITY_STATE_COMPONENT(EntityStateComponent.NAME, EntityStateComponent.class),
    /**
     * The mesh of a component (which might not be used only in the GUI).
     */
    MESH_COMPONENT(MeshComponent.NAME, MeshComponent.class),
    /**
     * 
     */
    COLLIDER_COMPONENT(ColliderComponent.NAME, ColliderComponent.class),
    /**
     *
     */
    TRANSFORM_COMPONENT(TransformComponent.NAME, TransformComponent.class);

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
