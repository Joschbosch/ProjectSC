/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.ui;

import de.projectsc.modes.client.gui.data.UI;

public interface GUIElement {

    void render(UI ui);

    boolean isVisible();

    void setVisible(boolean value);

    boolean isActive();

    void setActive(boolean active);
}
