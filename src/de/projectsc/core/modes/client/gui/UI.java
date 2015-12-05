/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.modes.client.gui;

import java.util.LinkedList;
import java.util.List;

import de.projectsc.core.modes.client.gui.ui.UITexture;

public class UI {

    List<UITexture> uiElements = new LinkedList<>();

    public void addElement(UITexture bg) {
        uiElements.add(bg);
    }

    public List<UITexture> getUIElements() {
        return uiElements;
    }

}
