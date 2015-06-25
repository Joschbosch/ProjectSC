/*
 * Copyright (C) 2015
 */

package de.projectsc.client.network;

import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.client.core.data.messages.MessageConstants;
import de.projectsc.client.core.data.messages.NetworkMessage;

public class ClientNetworkCore implements Runnable {

    private static final Log LOGGER = LogFactory.getLog(ClientNetworkCore.class);

    private final BlockingQueue<NetworkMessage> retreiveMessageQueue;

    private final BlockingQueue<NetworkMessage> sendMessageQueue;

    private boolean running = false;

    public ClientNetworkCore(BlockingQueue<NetworkMessage> networkIncomingQueue, BlockingQueue<NetworkMessage> networkOutgoingQueue) {
        this.sendMessageQueue = networkIncomingQueue;
        this.retreiveMessageQueue = networkOutgoingQueue;
    }

    private void start() {
        LOGGER.debug("Starting network ...");
        running = true;
        while (running) {
            retreiveCoreMessages();
        }
    }

    private void retreiveCoreMessages() {
        while (!sendMessageQueue.isEmpty()) {
            NetworkMessage msg = sendMessageQueue.poll();
            if (msg.getMessage().equals(MessageConstants.CLOSE_DOWN)) {
                shutdown();
            }
        }
    }

    private void shutdown() {
        running = false;
    }

    @Override
    public void run() {
        start();
    }

}
