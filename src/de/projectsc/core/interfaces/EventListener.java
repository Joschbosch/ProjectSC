/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.interfaces;

import de.projectsc.core.data.Event;

/**
 * Interface that every eventlistener must implements.
 * 
 * @author Josch Bosch
 */
public interface EventListener {

    /**
     * Process an event .
     * 
     * @param e to process
     */
    void processEvent(Event e);

    /**
     * Get listener source.
     * 
     * @return source
     */
    Class<?> getSource();
}
