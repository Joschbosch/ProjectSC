/*
 * Copyright (C) 2015
 */

package de.projectsc.core.interfaces;

import de.projectsc.core.data.Event;

/**
 * Interface for all kinds of system that exist in the engine.
 * 
 * @author Josch Bosch
 */
public interface EngineSystem {

    /**
     * Process an event .
     * 
     * @param e to process
     */
    void processEvent(Event e);

    /**
     * @return name of the system
     */
    String getName();

    /**
     * Update system with the given tick time.
     * 
     * @param tick time to update
     */
    void update(long tick);

}
