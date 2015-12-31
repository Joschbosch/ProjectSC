/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.ui;

import java.util.LinkedList;
import java.util.List;

/**
 * Manager for the UI element.
 * 
 * @author Josch Bosch
 */
public final class UIManager {

    private static List<UIElement> registeredElements = new LinkedList<>();

    private UIManager() {}

    /**
     * Register a new element.
     * 
     * @param uiElement to register
     */
    public static void registerElement(UIElement uiElement) {
        registeredElements.add(uiElement);
    }

    /**
     * Remove an element.
     * 
     * @param uiElement to remove
     */
    public static void unregisterElement(UIElement uiElement) {
        registeredElements.remove(uiElement);

    }

    /**
     * Ger element of specified class.
     * 
     * @param clazz to get
     * @return the element.
     */
    public static UIElement getElement(Class<? extends UIElement> clazz) {
        for (UIElement element : registeredElements) {
            if (clazz.isInstance(element)) {
                return element;
            }
        }
        return null;

    }
}
