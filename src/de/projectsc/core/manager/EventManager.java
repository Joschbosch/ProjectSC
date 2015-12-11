/*
 * Copyright (C) 2015
 */

package de.projectsc.core.manager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.data.Event;
import de.projectsc.core.interfaces.EngineSystem;

/**
 * This class is the heart of the event system for the engine. Events are fired up and distributed
 * to all listeners.
 * 
 * @author Josch Bosch
 */
public final class EventManager {

    private static final Log LOGGER = LogFactory.getLog(EventManager.class);

    private static Map<Class<? extends Event>, List<EngineSystem>> eventListener = new HashMap<>();

    private EventManager() {}

    /**
     * Fires an Event.
     * 
     * @param e to fire.
     */
    public static void fireEvent(Event e) {
        if (eventListener.get(e.getClass()) != null) {
            for (EngineSystem s : eventListener.get(e.getClass())) {
                s.processEvent(e);
            }
        }
    }

    /**
     * Register for an event type.
     * 
     * @param eventClass to register to
     * @param system that listens
     */
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
