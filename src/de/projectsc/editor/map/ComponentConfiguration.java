/*
 * Copyright (C) 2016 
 */

package de.projectsc.editor.map;

import javax.swing.JPanel;

import de.projectsc.core.interfaces.Component;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;

public abstract class ComponentConfiguration extends JPanel {

    protected EventManager eventManager;

    protected EntityManager entityManager;

    protected String entity;

    protected Component component;

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public abstract void init();
}
