/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.interfaces;

import java.util.List;
import java.util.Map;

import de.projectsc.modes.client.messages.ClientMessage;
import de.projectsc.modes.client.ui.BasicUIElement;

/**
 * The states for a Client.
 * 
 * @author Josch Bosch
 */
public interface ClientState {

    /**
     * Handles a new message from the server or internally.
     * 
     * @param msg to handle
     */
    void handleMessage(ClientMessage msg);

    /**
     * 
     * Does one loop iteration with the given time.
     * 
     * @param tickTime that needs to be calculated.
     */
    void loop(long tickTime);

    /**
     * This method returns all UI elements that need to be rendered by the GUI.
     * 
     * @return all ui elements
     */
    List<BasicUIElement> getUI();

    /**
     * Initialize the state.
     * 
     * @param gui the {@link GUI} used for this client.
     */
    void init(GUI gui);

    /**
     * Handles input that only concerns this state, e.g. a button click.
     * 
     * @param keyMap current keystrokes
     */
    void handleInput(Map<Integer, Integer> keyMap);
}
