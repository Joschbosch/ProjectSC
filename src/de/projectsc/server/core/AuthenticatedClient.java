/*
 * Copyright (C) 2015 
 */

package de.projectsc.server.core;

import java.util.concurrent.BlockingQueue;

import de.projectsc.server.core.serverMessages.ServerMessage;

public class AuthenticatedClient {

    private final String displayName;

    private final long id;

    private final BlockingQueue<ServerMessage> sendToClientQueue;

    private final BlockingQueue<ServerMessage> receiveFromClientQueue;

    public AuthenticatedClient(long id, String displayName, BlockingQueue<ServerMessage> sendToClientQueue,
        BlockingQueue<ServerMessage> receiveFromClientQueue) {
        this.id = id;
        this.displayName = displayName;
        this.sendToClientQueue = sendToClientQueue;
        this.receiveFromClientQueue = receiveFromClientQueue;
    }

    public void sendMessage(ServerMessage msg) {
        sendToClientQueue.offer(msg);
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

    public String getDisplayName() {
        return displayName;
    }
}
