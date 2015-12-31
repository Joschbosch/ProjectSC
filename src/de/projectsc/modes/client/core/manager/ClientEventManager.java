/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.manager;

import de.projectsc.core.manager.EventManager;

/**
 * Extending class for the {@link EventManager} in the client. (There is only one for every client)
 * 
 * @author Josch Bosch
 */
public class ClientEventManager extends EventManager {

    private static ClientEventManager instance;

    public ClientEventManager() {
        setInstance(this);
    }

    public static ClientEventManager getInstance() {
        return instance;
    }

    public static void setInstance(ClientEventManager instance) {
        ClientEventManager.instance = instance;
    }
}
