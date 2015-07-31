/*
 * Copyright (C) 2015 
 */
 
package de.projectsc.server.core.client;

import java.util.concurrent.BlockingQueue;

import de.projectsc.server.core.messages.ServerMessage;

/**
 * Special case of {@link Client} for authentification.
 *
 * @author David Scholz
 */
public class AuthenticatedClient extends Client {
    
    private String displayName;

    private  long id;

    private  BlockingQueue<ServerMessage> sendToClientQueue;

    private  BlockingQueue<ServerMessage> receiveFromClientQueue;
    
    public AuthenticatedClient(String displayName, long id, BlockingQueue<ServerMessage> sendToClientQueue, BlockingQueue<ServerMessage> receiveFromClienQueue) {
        super(displayName, id);
        this.sendToClientQueue = sendToClientQueue;
        this.receiveFromClientQueue = receiveFromClienQueue;
    }
    
    public BlockingQueue<ServerMessage> getSendToClientQueue() {
        return sendToClientQueue;
    }
    
    public BlockingQueue<ServerMessage> getReceiveFromClientQueue() {
        return receiveFromClientQueue;
    }

    @Override
    public void sendMessage(ServerMessage msg) {
        sendToClientQueue.offer(msg);
    }

    @Override
    public void received(ServerMessage msg) {
        System.out.println("Received message from client: " + msg);
        receiveFromClientQueue.add(msg);
    }

}
