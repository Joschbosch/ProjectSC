/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui.data;

import de.projectsc.modes.client.ui.BasicUIElement;

/**
 * This is the parent class for all UI element representations in the GUI.
 * 
 * @author Josch Bosch
 */
public abstract class View {

    protected BasicUIElement element;

    public View(BasicUIElement element) {
        this.element = element;
    }

    /**
     * Add all elements to render for the current frame.
     * 
     * @param ui container to add elements
     */
    public abstract void render(UI ui);
}
