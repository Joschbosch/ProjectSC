/*
 * Copyright (C) 2015 
 */
 
package de.projectsc.server.core.messages;

import com.esotericsoftware.kryonet.Connection;

/**
 * Message if new client connects.
 *
 * @author David Scholz
 */
public class NewClientConnectedServerMessage extends ServerMessage {
    
    private final Connection connection;

    public NewClientConnectedServerMessage(Connection client) {
        this.connection = client;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public String getMessage() {
        // TODO Auto-generated method stub
        return null;
    }

}
