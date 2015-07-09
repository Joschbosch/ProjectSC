/*
 * Copyright (C) 2015 
 */

package de.projectsc.server.core.serverMessages;

import de.projectsc.server.core.AuthenticatedClient;

public class NewClientConnectedServerMessage extends ServerMessage {

    private AuthenticatedClient client;

    public NewClientConnectedServerMessage(String msg, AuthenticatedClient client, Object[] data) {
        super(msg, data);
        this.client = client;
    }

    public AuthenticatedClient getClient() {
        return client;
    }
}
