/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.core.interfaces;

import java.util.Queue;

import de.projectsc.core.data.InputCommand;

/**
 * Interface for GUI implementations.
 * 
 * @author Josch Bosch
 */
public interface GUI {

    /**
     * Render gui.
     * 
     */
    void render();

    /**
     * Read user input.
     * 
     * @return messages for core.
     */
    Queue<InputCommand> readInput();

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
     * @return true, if the GUI is still running
     */
    boolean isRunning();

    /**
     * Clean up everything after program is done.
     */
    void cleanUpCore();

    void loadTerrain();

    void initState(ClientState newState);
}
