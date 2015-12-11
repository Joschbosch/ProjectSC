/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.common;

import java.util.List;
import java.util.Map;

import de.projectsc.modes.client.common.data.UIElement;
import de.projectsc.modes.client.common.messages.ClientMessage;

/**
 * The states for a Client.
 * 
 * @author Josch Bosch
 */
public abstract class ClientState {

    /**
     * Handles a new message from the server or internally.
     * 
     * @param msg to handle
     */
    public abstract void handleMessage(ClientMessage msg);

    /**
     * 
     * Does one loop iteration with the given time.
     * 
     * @param tickTime that needs to be calculated.
     */
    public abstract void loop(long tickTime);

    /**
     * This method returns all UI elements that need to be rendered by the GUI.
     * 
     * @return all ui elements
     */
    public abstract List<UIElement> getUI();

    /**
     * Initialize the state.
     * 
     * @param gui the {@link GUI} used for this client.
     */
    public abstract void init(GUI gui);

    /**
     * Handles input that only concerns this state, e.g. a button click.
     * 
     * @param keyMap current keystrokes
     */
    public abstract void handleInput(Map<Integer, Integer> keyMap);
}
