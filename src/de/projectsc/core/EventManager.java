/*
 * Copyright (C) 2015
 */

package de.projectsc.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.data.Event;

public class EventManager {

    private static final Log LOGGER = LogFactory.getLog(EventManager.class);

    private static Map<Class<? extends Event>, List<EngineSystem>> eventListener = new HashMap<>();

    public static void fireEvent(Event e) {
        if (eventListener.get(e.getClass()) != null) {
            for (EngineSystem s : eventListener.get(e.getClass())) {
                s.processEvent(e);
            }
        }
    }

    public static void registerForEvent(Class<? extends Event> eventClass, EngineSystem system) {
        List<EngineSystem> eventList = eventListener.get(eventClass);
        if (eventList == null) {
            eventList = new LinkedList<>();
            eventListener.put(eventClass, eventList);
        }
        eventList.add(system);
        LOGGER.debug("Registered listener for " + eventClass.getName() + " to system " + system.getName());
    }
}
