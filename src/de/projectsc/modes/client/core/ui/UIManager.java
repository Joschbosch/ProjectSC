/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.ui;

import java.util.LinkedList;
import java.util.List;

public class UIManager {

    private static List<UIElement> registeredElements = new LinkedList<>();

    public static void registerElement(UIElement uiElement) {
        registeredElements.add(uiElement);
    }

    public static void unregisteElement(UIElement uiElement) {
        registeredElements.remove(uiElement);

    }

    public static UIElement getElement(Class<? extends UIElement> clazz) {
        for (UIElement element : registeredElements) {
            if (clazz.isInstance(element)) {
                return element;
            }
        }
        return null;

    }
}
