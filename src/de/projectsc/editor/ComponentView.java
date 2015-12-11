/*
 * Copyright (C) 2015
 */

package de.projectsc.editor;

import javax.swing.JDialog;

import de.projectsc.core.interfaces.Component;

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

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        getAssociatedComponent().setActive(b);
    }

    protected abstract Component getAssociatedComponent();
}
