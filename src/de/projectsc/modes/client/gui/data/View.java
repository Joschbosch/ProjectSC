/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui.data;

import de.projectsc.modes.client.gui.ui.basic.Container;

/**
 * This is the parent class for all UI element representations in the GUI.
 * 
 * @author Josch Bosch
 */
public abstract class View {

    private Container container;

    public View(Container c) {
        this.container = c;
    }

    /**
     * Add all elements to render for the current frame.
     * 
     * @param ui container to add elements
     */
    public abstract void update();
}
