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
    
    public NewAuthenticatedClientServerMessage() {

    }

    @Override
    public Connection getConnection() {
        return null;
    }

}
