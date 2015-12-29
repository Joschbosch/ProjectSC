/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component;

import de.projectsc.core.component.collision.ColliderComponent;
import de.projectsc.core.component.physic.MeshComponent;
import de.projectsc.core.component.physic.PathComponent;
import de.projectsc.core.component.physic.TransformComponent;
import de.projectsc.core.component.physic.VelocityComponent;
import de.projectsc.core.component.state.EntityStateComponent;

/**
 * These are all (non GUI) components that are known in the engine.
 *
 * @author Josch Bosch
 */
public enum ComponentListItem {
    /**
     * Component for movement.
     */
    VELOCITY_COMPONENT(VelocityComponent.NAME, VelocityComponent.class, true),
    /**
     * Represents the state of an entity.
     */
    ENTITY_STATE_COMPONENT(EntityStateComponent.NAME, EntityStateComponent.class, true),
    /**
     * The mesh of a component (which might not be used only in the GUI).
     */
    MESH_COMPONENT(MeshComponent.NAME, MeshComponent.class, true),
    /**
     * 
     */
    COLLIDER_COMPONENT(ColliderComponent.NAME, ColliderComponent.class, true),
    /**
     *
     */
    TRANSFORM_COMPONENT(TransformComponent.NAME, TransformComponent.class, true),
    /**
    *
    */
    PATH_COMPONENT(PathComponent.NAME, PathComponent.class, false);

    private String name;

    private Class<? extends DefaultComponent> clazz;

    private boolean addAble;

    ComponentListItem(String name, Class<? extends DefaultComponent> clazz, boolean addAble) {
        this.setName(name);
        this.setClazz(clazz);
        this.addAble = addAble;
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

    public boolean isAddAble() {
        return addAble;
    }
}
