/*
 * Copyright (C) 2015 
 */
 
package de.projectsc.server.core.messages;

import com.esotericsoftware.kryonet.Connection;

/**
 *
 * Server response to {@link AuthentificationRequestServerMessage}.
 *
 * @author David Scholz
 */
public class AuthentificationResponseServerMessage implements ServerMessage {
    
    private final Connection client;
    
    private final boolean accepted;
    
    public AuthentificationResponseServerMessage(Connection client, boolean accepted) {
        this.client = client;
        this.accepted = accepted;
    }
    
    public boolean isValid() {
        return accepted;
    }

    @Override
    public Connection getConnection() {
        return client;
    }

}