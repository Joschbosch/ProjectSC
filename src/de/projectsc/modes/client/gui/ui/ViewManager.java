/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.ui;

import java.util.LinkedList;
import java.util.List;

import de.projectsc.modes.client.gui.data.View;

public class ViewManager {

    private static List<View> registeredElements = new LinkedList<>();

    public static void registerView(View view) {
        registeredElements.add(view);
    }

    public static void unregisteElement(View view) {
        registeredElements.remove(view);

    }

    public static View getElement(Class<? extends View> clazz) {
        for (View element : registeredElements) {
            if (clazz.isInstance(element)) {
                return element;
            }
        }
        return null;

    }
}
