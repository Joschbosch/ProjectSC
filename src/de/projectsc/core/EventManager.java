/*
 * Copyright (C) 2015
 */

package de.projectsc.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.projectsc.core.data.Event;
import de.projectsc.core.data.entities.Component;

public class EventManager {

    private static Map<String, List<Component>> eventListener = new HashMap<>();

    public static void fireEvent(Event e) {
        if (eventListener.get(e.getID()) != null) {
            for (Component c : eventListener.get(e.getID())) {
                c.processEvent(e);
            }
        }
    }

    public static void registerForEvent(String id) {}
}
