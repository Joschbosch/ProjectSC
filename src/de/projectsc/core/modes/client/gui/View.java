/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.modes.client.gui;

import de.projectsc.core.modes.client.common.UIElement;

public abstract class View {

    protected UIElement element;

    public View(UIElement element) {
        this.element = element;
    }

    public abstract void render(UI ui);
}
