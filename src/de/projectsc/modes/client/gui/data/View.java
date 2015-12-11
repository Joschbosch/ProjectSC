/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui.data;

import de.projectsc.modes.client.common.data.UIElement;

/**
 * This is the parent class for all UI element representations in the GUI.
 * 
 * @author Josch Bosch
 */
public abstract class View {

    protected UIElement element;

    public View(UIElement element) {
        this.element = element;
    }

    /**
     * Add all elements to render for the current frame.
     * 
     * @param ui container to add elements
     */
    public abstract void render(UI ui);
}
