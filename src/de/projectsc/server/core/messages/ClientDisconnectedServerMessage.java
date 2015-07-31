/*
 * Copyright (C) 2015 
 */
 
package de.projectsc.server.core.messages;

import com.esotericsoftware.kryonet.Connection;

/**
 * 
 * Message if client disconnects.
 *
 * @author David Scholz
 */
public class ClientDisconnectedServerMessage extends ServerMessage {
    
    public ClientDisconnectedServerMessage() {
        
    }

    @Override
    public Connection getConnection() {
        return null;
    }

}
