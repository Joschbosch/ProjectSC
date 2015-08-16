/*
 * Project SC-2015 
 */
 
package de.projectsc.server.core.messages;

import com.esotericsoftware.kryonet.Connection;

/**
 * 
 * {@link ServerMessage} to request the creation of a new client.
 *
 * @author David Scholz
 */
public class CreateNewClientRequestMessage implements ServerMessage {
    
    private Connection client;
    
    public CreateNewClientRequestMessage(Connection client) {
        this.client = client;
    }

    @Override
    public Connection getConnection() {
        return client;
    }

}
