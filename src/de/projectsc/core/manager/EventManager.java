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
import de.projectsc.core.interfaces.EventListener;

/**
 * This class is the heart of the event system for the engine. Events are fired up and distributed to all listeners.
 * 
 * @author Josch Bosch
 */
public class EventManager {

    private static final Log LOGGER = LogFactory.getLog(EventManager.class);

    private Map<Class<? extends Event>, List<EventListener>> eventListener = new HashMap<>();

    /**
     * Fires an Event.
     * 
     * @param e to fire.
     */
    public void fireEvent(Event e) {
        if (eventListener.get(e.getClass()) != null) {
            for (EventListener s : eventListener.get(e.getClass())) {
                s.processEvent(e);
            }
        }
    }

    /**
     * Register for an event type.
     * 
     * @param eventClass to register to
     * @param listener that listens
     */
    public void registerForEvent(Class<? extends Event> eventClass, EventListener listener) {
        List<EventListener> eventList = eventListener.get(eventClass);
        if (eventList == null) {
            eventList = new LinkedList<>();
            eventListener.put(eventClass, eventList);
        }
        eventList.add(listener);
        LOGGER.info("Registered listener for " + eventClass.getName() + " to " + listener.getSource());
    }
}
