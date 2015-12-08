/*
 * Copyright (C) 2015 
 */
package de.projectsc.core.modes.client.gui.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.projectsc.core.modes.client.gui.textures.UITexture;

public class UI {

    public static final Integer BEFORE_TEXT = 0;

    public static final Integer AFTER_TEXT = 1;

    Map<Integer, List<UITexture>> uiElements = new HashMap<>();

    public void addElement(UITexture uiElement, Integer order) {
        List<UITexture> orderedTextures = uiElements.get(uiElements);
        if (orderedTextures == null) {
            orderedTextures = new LinkedList<>();
            uiElements.put(order, orderedTextures);
        }
        orderedTextures.add(uiElement);
    }

    public List<UITexture> getUIElements(Integer order) {
        List<UITexture> orderedElements = uiElements.get(order);
        if (orderedElements == null) {
            return new LinkedList<>();
        }
        return orderedElements;
    }

}
