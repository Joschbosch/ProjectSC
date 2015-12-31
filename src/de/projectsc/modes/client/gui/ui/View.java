/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui.ui;

import de.projectsc.modes.client.gui.data.UI;
import de.projectsc.modes.client.gui.ui.basic.BasicGUIElement;
import de.projectsc.modes.client.gui.ui.basic.Container;

/**
 * This is the parent class for all UI element representations in the GUI.
 * 
 * @author Josch Bosch
 */
public abstract class View extends BasicGUIElement {

    protected Container container;

    public View(Container c) {
        this.container = c;
    }

    /**
     * Add all elements to render for the current frame.
     * 
     */
    public abstract void update();

    @Override
    public void render(UI ui) {
        if (container != null) {
            container.render(ui);
        }
    }

    @Override
    public boolean isVisible() {
        return container.isVisible();
    }

    @Override
    public void setVisible(boolean value) {
        container.setVisible(value);
    }
}
