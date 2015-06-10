/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.states;

/**
 * Interface for all gui states.
 * 
 * @author Josch Bosch
 */
public interface State {

    /**
     * Initialize the state.
     */
    void initialize();

    /**
     * Pause state.
     */
    void pause();

    /**
     * Resume state.
     *
     */
    void resume();

    /**
     * Update state.
     */
    void update();

    /**
     * Render state.
     */
    void render();
}
