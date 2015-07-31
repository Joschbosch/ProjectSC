/*
 * Copyright (C) 2015 
 */
 
package de.projectsc.server.core.messages;

import com.esotericsoftware.kryonet.Connection;

/**
 * 
 * New {@link Client} sends authentification request to server.
 *
 * @author David Scholz
 */
public class AuthentificationRequestServerMessage extends ServerMessage {
    
    private final Connection client;
    
    public AuthentificationRequestServerMessage(Connection connection) {
        this.client = connection;
    }
    
    public Connection getClient() {
        return client;
    }

    @Override
    public Connection getConnection() {
        return client;
    }

}
