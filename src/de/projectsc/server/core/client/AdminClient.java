/*
 * Copyright (C) 2015 
 */
 
package de.projectsc.server.core.client;

import java.util.concurrent.BlockingQueue;

import de.projectsc.server.core.messages.ServerMessage;

/**
 * Admin client.
 *
 * @author David Scholz
 */
public class AdminClient extends Client {
    
    private String displayName;

    private  long id;

    private  BlockingQueue<ServerMessage> sendToClientQueue;

    private  BlockingQueue<ServerMessage> receiveFromClientQueue;
    
    public AdminClient(String displayName, long id, BlockingQueue<ServerMessage> sendToClientQueue, BlockingQueue<ServerMessage> receiveFromClientQueue) {
        super(displayName, id,  sendToClientQueue, receiveFromClientQueue);
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
