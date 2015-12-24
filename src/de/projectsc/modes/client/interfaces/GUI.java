/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.interfaces;

import java.util.Queue;

import de.projectsc.core.data.InputCommand;
import de.projectsc.modes.client.ui.BasicUIElement;

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
     */
    void render(ClientState state);

    /**
     * Read user input.
     * 
     * @return messages for core.
     */
    Queue<InputCommand> readInput();

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

    /**
     * Clean up the current state.
     * 
     * @param state to clean up
     */
    void cleanUpState(ClientState state);

    /**
     * @return true, if the GUI is still running
     */
    boolean isRunning();

    /**
     * Register a new view for an UIElement.
     * 
     * @param element to register a view
     */
    void registerView(BasicUIElement element);

    /**
     * Unregister view for the given element.
     * 
     * @param element to unregister
     */
    void unregisterView(BasicUIElement element);

    /**
     * Clean up everything after program is done.
     */
    void cleanUpCore();

    void loadTerrain();
}
