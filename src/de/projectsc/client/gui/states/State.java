/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.client.gui.states;

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
     * 
     * @param elapsedTime since the last frame
     */
    void render(long elapsedTime);

    /**
     * handle Input.
     * 
     * @param elapsedTime since the last frame
     */
    void handleInput(long elapsedTime);

    /**
     * Terminate status.
     */
    void terminate();
}
