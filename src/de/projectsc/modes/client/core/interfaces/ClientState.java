/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.core.interfaces;

import java.util.concurrent.BlockingQueue;

import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.modes.client.core.data.ClientMessage;

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
     * Initialize the state.
     * 
     * @param gameData
     * @param componentManager
     * @param eventManager
     * @param entityManager
     */
    void init(BlockingQueue<ClientMessage> networkQueue, EntityManager entityManager, EventManager eventManager,
        ComponentManager componentManager);

    String getId();

}
