/*
 * Copyright (C) 2015 
 */
 
package de.projectsc.server.core.messages;

import com.esotericsoftware.kryonet.Connection;

/**
 * Client authenticated message.
 *
 * @author David Scholz
 */
public class NewAuthenticatedClientServerMessage extends ServerMessage {
    
    private final Connection connection;
    
    public NewAuthenticatedClientServerMessage(Connection client) {
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
