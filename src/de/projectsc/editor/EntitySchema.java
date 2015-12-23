/*
 * Copyright (C) 2015 
 */

package de.projectsc.editor;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;

public class EntitySchema {

    private Long id;

    List<Component> components = new LinkedList<>();

    public EntitySchema(long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void createNewEntity(Transform oldTransform, long e) {
        Transform t = EntityManager.getEntity(e).getTransform();
        t.setPosition(new Vector3f(oldTransform.getPosition()));
        t.setRotation(new Vector3f(oldTransform.getRotation()));
        t.setScale(new Vector3f(oldTransform.getScale()));
        for (Component c : components) {
            Component clone = ComponentManager.cloneComponent(c);
            EntityManager.addComponentToEntity(e, clone);
        }
        EntityManager.getEntity(e).setEntityTypeId(id);
    }
}
