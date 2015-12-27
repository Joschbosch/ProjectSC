/*
 * Copyright (C) 2015
 */

package de.projectsc.core.interfaces;


/**
 * Interface for all kinds of system that exist in the engine.
 * 
 * @author Josch Bosch
 */
public interface EngineSystem extends EventListener {

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
