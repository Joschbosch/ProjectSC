/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.client.core;

import java.util.Map;

import de.projectsc.client.core.states.ClientState;
import de.projectsc.client.core.ui.elements.Snapshot;

/**
 * Interface for GUI implementations.
 * 
 * @author Josch Bosch
 */
public interface GUI {

    /**
     * @return true, if successful.
     */
    boolean init();

    /**
     * Render gui.
     * 
     * @param state to render
     * @param data to render
     */
    void render(ClientState state, Snapshot data);

    /**
     * Read user input.
     * 
     * @return messages for core.
     */
    Map<Integer, Integer> readInput();

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
     * handle Input.
     * 
     */
    void handleInput();

    /**
     * Terminate status.
     */
    void terminate();

    boolean initCore();

    boolean initState(ClientState state);

    boolean isRunning();
}
