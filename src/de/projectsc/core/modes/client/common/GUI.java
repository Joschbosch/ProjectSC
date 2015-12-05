/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.modes.client.common;

import java.util.Map;

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

    /**
     * Initialize the GUI Core. This is done once at the startup of the client.
     * 
     * @return true, if successful
     */
    boolean initCore();

    /**
     * This initializes a new state.
     * 
     * @param state to init
     * @return true, if successful
     */
    boolean initState(ClientState state);

    void cleanUpState(ClientState state);

    /**
     * @return true, if the GUI is still running
     */
    boolean isRunning();

    void registerView(UIElement element);

    void unregisterView(UIElement element);
}
