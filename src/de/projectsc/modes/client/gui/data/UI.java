/*
 * Copyright (C) 2015 
 */
package de.projectsc.modes.client.gui.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.projectsc.modes.client.gui.textures.UITexture;

/**
 * Container for all UI elements.
 * 
 * @author Josch Bosch
 */
public class UI {

    /**
     * 
     */
    public static final Integer BEFORE_TEXT = 0;

    /**
     * 
     */
    public static final Integer AFTER_TEXT = 1;

    private Map<Integer, List<UITexture>> uiElements = new HashMap<>();

    /**
     * 
     * @param uiElement to add
     * @param order of element
     */
    public void addElement(UITexture uiElement, Integer order) {
        List<UITexture> orderedTextures = uiElements.get(uiElements);
        if (orderedTextures == null) {
            orderedTextures = new LinkedList<>();
            uiElements.put(order, orderedTextures);
        }
        orderedTextures.add(uiElement);
    }

    /**
     * @param order to render
     * @return list of all UI textures
     */

    public List<UITexture> getUIElements(Integer order) {
        List<UITexture> orderedElements = uiElements.get(order);
        if (orderedElements == null) {
            return new LinkedList<>();
        }
        return orderedElements;
    }

}
