/*
 * Copyright (C) 2015 
 */

package de.projectsc.editor;

import javax.swing.JDialog;

import de.projectsc.core.entities.Component;

/**
 * 
 * @author Josch Bosch
 */
public abstract class ComponentView extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    protected long entity;

    public ComponentView() {
        setSize(getInitialWidth(), getInitialHeight());
    }

    protected abstract int getInitialHeight();

    protected abstract int getInitialWidth();

    public void setEntity(long entity) {
        this.entity = entity;
    }

    protected abstract Component getAssociatedComponent();
}
