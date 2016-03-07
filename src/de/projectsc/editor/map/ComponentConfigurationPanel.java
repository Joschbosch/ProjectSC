/*
 * Copyright (C) 2016 
 */

package de.projectsc.editor.map;

import javax.swing.JPanel;

import de.projectsc.core.interfaces.Component;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;

/**
 * Configuration panel for a component configuration.
 * 
 * @author Josch Bosch
 */
public abstract class ComponentConfigurationPanel extends JPanel {

    private static final long serialVersionUID = 1609554163282353449L;

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
    /**
     * Initialize the panel.
     */
    public abstract void init();
}
