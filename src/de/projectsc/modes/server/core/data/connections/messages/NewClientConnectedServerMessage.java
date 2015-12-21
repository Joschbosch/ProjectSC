/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.server.core.data.connections.messages;

import de.projectsc.modes.server.core.data.AuthenticatedClient;
import de.projectsc.modes.server.core.messages.ServerMessage;

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
