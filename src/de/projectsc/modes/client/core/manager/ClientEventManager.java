/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.manager;

import de.projectsc.core.manager.EventManager;

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
