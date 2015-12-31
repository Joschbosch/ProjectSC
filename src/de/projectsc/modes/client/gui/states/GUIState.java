/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.states;

import de.projectsc.modes.client.gui.data.UI;

/**
 * Interface for all states of the GUI.
 * 
 * @author Josch Bosch
 */
public interface GUIState {

    /**
     * Check if the scene should be rendered or just UI.
     * 
     * @return true if it should be rendered.
     */
    boolean renderScene();

    /**
     * Init state.
     */
    void initialize();

    /**
     * Update state.
     */
    void update();

    /**
     * Get all UI elements from this state.
     * 
     * @param ui to write elements to.
     */
    void getUIElements(UI ui);

    /**
     * Dispose state.
     */
    void tearDown();

    /**
     * @return true if camera should be moveable by the player.
     */
    boolean getCameraMoveable();

    /**
     * 
     * @return true if debug is active.
     */
    boolean isDebugModeActive();

}
