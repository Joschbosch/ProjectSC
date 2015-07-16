/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.core;

import au.com.ds.ef.StatefulContext;

/**
 * Context for all game states.
 * 
 * @author Josch Bosch
 */
public class ClientGameContext extends StatefulContext {

    private final ClientCore core;

    public ClientGameContext(ClientCore core) {
        this.core = core;
    }

    public ClientCore getCore() {
        return core;
    }

}
