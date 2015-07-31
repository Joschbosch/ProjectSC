/*
 * Copyright (C) 2015 
 */
 
package de.projectsc.server.core.client;

import de.projectsc.server.core.messages.ServerMessage;

/**
 * Abstract class for Clients.
 *
 * @author David Scholz
 */
public abstract class Client {
    
    private final String displayName;

    private final long id;
    
    public Client(String displayName, long id) {
        this.displayName = displayName;
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public long getId() {
        return id;
    }
    
    public abstract void sendMessage(ServerMessage msg);
    
    public abstract void received(ServerMessage msg);
    
}
