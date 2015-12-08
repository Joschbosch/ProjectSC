/*
 * Copyright (C) 2015
 */

package de.projectsc.core.modes.client.gui.data;

import java.util.LinkedList;
import java.util.List;

import de.projectsc.core.modes.client.gui.textures.UITexture;

/**
 * A container class for all UI elements for the current frame.
 * 
 * @author Josch Bosch
 */
public class UI {

    private final List<UITexture> uiElements = new LinkedList<>();

    /**
     * Add a new UI element to render.
     * 
     * @param element to render
     */
    public void addElement(UITexture element) {
        uiElements.add(element);
    }

    public List<UITexture> getUIElements() {
        return uiElements;
    }

}
