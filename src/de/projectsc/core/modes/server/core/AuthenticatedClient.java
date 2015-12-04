/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.modes.server.core;

import java.util.concurrent.BlockingQueue;

import de.projectsc.core.modes.server.core.messages.ServerMessage;

/**
 * An instance of the client that is already authenticated in the network. Contains everything neede
 * to identify and contact the client.
 * 
 * @author Josch Bosch
 */
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

    /**
     * Send a message to the client.
     * 
     * @param msg to send.
     */
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

    /**
     * @param arg1 received servermessage
     */
    public void received(ServerMessage arg1) {
        receiveFromClientQueue.add(arg1);
    }
}
