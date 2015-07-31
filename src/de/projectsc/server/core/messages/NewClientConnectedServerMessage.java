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

    public NewClientConnectedServerMessage() {

    }

    @Override
    public Connection getConnection() {
        return null;
    }

}
