/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.core.interfaces;

import java.util.concurrent.BlockingQueue;

import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.modes.client.core.data.ClientMessage;
import de.projectsc.modes.client.core.manager.ClientSnapshotManger;

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
     * @return new state if necessary
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
     * @param networkQueue for network msgs.
     * @param entityManager for managing.
     * @param eventManager for managing.
     * @param componentManager for managing.
     * @param snapshotManager for managing.
     * @param timer for current deltas.
     */
    void init(BlockingQueue<ClientMessage> networkQueue, EntityManager entityManager, EventManager eventManager,
        ComponentManager componentManager, ClientSnapshotManger snapshotManager, Timer timer);

    /**
     * @return if of the state
     */
    String getId();

}
