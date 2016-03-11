/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.entities;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.physic.TransformComponent;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.manager.EntityManager;

/**
 * Schema of an entity created in the editor.
 * 
 * @author Josch Bosch
 */
public class EntitySchema {

    private Long id;

    private List<Component> components = new LinkedList<>();

    public EntitySchema(long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    /**
     * Create entity from schema.
     * 
     * @param oldTransform of mouse position
     * @param e to add schmea to
     * @param entityManager manager.
     */
    public void createNewEntity(Transform oldTransform, String e, EntityManager entityManager) {
        for (Component c : components) {
            Component clone = c.cloneComponent();
            entityManager.addComponentToEntity(e, clone, false);
            clone.setOwner(entityManager.getEntity(e));
        }
        entityManager.getEntity(e).setEntityTypeId(id);
    }

    public List<Component> getComponents() {
        return components;
    }
}
