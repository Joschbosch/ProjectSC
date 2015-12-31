/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.ui;

import de.projectsc.modes.client.gui.data.UI;

/**
 * Interface for an element in the GUI.
 * 
 * @author Josch Bosch
 */
public interface GUIElement {

    /**
     * Add infos to render the element.
     * 
     * @param ui to add information to
     */
    void render(UI ui);

    /**
     * @return true, if element is visible
     */
    boolean isVisible();

    /**
     * @param value visibility.
     */
    void setVisible(boolean value);

    /**
     * @return true, if element is active.
     */
    boolean isActive();

    /**
     * @param active set activity
     */
    void setActive(boolean active);
}
