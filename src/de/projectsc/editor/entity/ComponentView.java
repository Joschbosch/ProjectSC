/*
 * Copyright (C) 2015
 */

package de.projectsc.editor.entity;

import javax.swing.JDialog;

import de.projectsc.core.interfaces.Component;
import de.projectsc.core.interfaces.Entity;
import de.projectsc.core.manager.EventManager;

/**
 * 
 * @author Josch Bosch
 */
public abstract class ComponentView extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected Entity entity;

    protected EventManager eventManager;

    public ComponentView() {
        setSize(getInitialWidth(), getInitialHeight());
    }

    protected abstract int getInitialHeight();

    protected abstract int getInitialWidth();

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        getAssociatedComponent().setActive(b);
    }

    protected abstract Component getAssociatedComponent();
}
