/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.interfaces;

import java.util.List;
import java.util.concurrent.BlockingQueue;

import de.projectsc.core.data.structure.Snapshot;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.modes.client.core.data.ClientGameContext;
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
     * @return
     */
    ClientState handleMessage(ClientMessage msg);

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
     * @param gameData
     * @param componentManager
     * @param eventManager
     * @param entityManager
     */
    void init(GUI gui, BlockingQueue<ClientMessage> networkQueue, EntityManager entityManager, EventManager eventManager,
        ComponentManager componentManager, ClientGameContext gameData);

    Snapshot getSnapshot();
}
