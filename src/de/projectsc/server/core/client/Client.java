/*
 * Copyright (C) 2015 
 */
 
package de.projectsc.server.core.client;

import java.util.concurrent.BlockingQueue;

import de.projectsc.server.core.messages.ServerMessage;

/**
 * Abstract class for Clients.
 *
 * @author David Scholz
 */
public abstract class Client {
    
    private final String displayName;

    private final long id;

    private final BlockingQueue<ServerMessage> sendToClientQueue;

    private final BlockingQueue<ServerMessage> receiveFromClientQueue;
    
    public Client(String displayName, long id, BlockingQueue<ServerMessage> sendToClientQueue, BlockingQueue<ServerMessage> receiveFormClientQueue) {
        this.displayName = displayName;
        this.id = id;
        this.sendToClientQueue = sendToClientQueue;
        this.receiveFromClientQueue = receiveFormClientQueue;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public long getId() {
        return id;
    }
    
    public BlockingQueue<ServerMessage> getSendToClientQueue() {
        return sendToClientQueue;
    }
    
    public BlockingQueue<ServerMessage> getReceiveFromClientQueue() {
        return receiveFromClientQueue;
    }
    
    public abstract void sendMessage(ServerMessage msg);
    
    public abstract void received(ServerMessage msg);
    
}
