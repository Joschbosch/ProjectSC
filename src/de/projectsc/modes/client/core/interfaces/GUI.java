/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.core.interfaces;

import de.projectsc.core.data.structure.Snapshot;

/**
 * Interface for GUI implementations.
 * 
 * @author Josch Bosch
 */
public interface GUI {

    void render();

    /**
     * Render gui.
     * 
     * @param interpolationTime
     * @param interpolationSnapshots
     * 
     */
    void render(Snapshot[] interpolationSnapshots, long interpolationTime);

    /**
     * Read user input.
     */
    void readInput();

    /**
     * Terminate status.
     */
    void terminate();

    /**
     * Initialize the GUI Core. This is done once at the startup of the client.
     * 
     * @return true, if successful
     */
    boolean init();

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
