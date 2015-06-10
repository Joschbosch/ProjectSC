/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui.states;

import java.util.concurrent.BlockingQueue;

import de.projectsc.gui.InputData;

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

    /**
     * handle Input.
     * 
     * @param input queue with current inputs
     */
    void handleInput(BlockingQueue<InputData> input);
}
