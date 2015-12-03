/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.client.core.states;

import java.util.List;
import java.util.Map;

import de.projectsc.client.core.data.Scene;
import de.projectsc.client.core.messages.ClientMessage;
import de.projectsc.client.gui.ui.UITexture;

/**
 * The states for a Client.
 * 
 * @author Josch Bosch
 */
public abstract class ClientState {

    public abstract void handleMessage(ClientMessage msg);

    public abstract void loop(long tickTime);

    public abstract Map<String, List<String>> getGUIObjectsToLoad();

    public abstract Scene getScene();

    public abstract List<UITexture> getUI();

    public abstract void init();

    public abstract void handleInput(Map<Integer, Integer> keyMap);
}
