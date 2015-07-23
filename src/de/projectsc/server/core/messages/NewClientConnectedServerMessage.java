/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.server.core.messages;

import de.projectsc.server.core.AuthenticatedClient;

/**
 * Special message if a new client conncted.
 * 
 * @author Josch Bosch
 */
public class NewClientConnectedServerMessage extends ServerMessage {

    private final AuthenticatedClient client;

    public NewClientConnectedServerMessage(String msg, AuthenticatedClient client, Object... data) {
        super(msg, data);
        this.client = client;
    }

    public AuthenticatedClient getClient() {
        return client;
    }
}
