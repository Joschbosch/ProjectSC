/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.network;

import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.client.core.messages.ClientMessage;
import de.projectsc.core.data.messages.MessageConstants;

/**
 * Core class for client network communication.
 *
 * @author Josch Bosch
 */
public class ClientNetworkCore implements Runnable {

    private static final int TICK_LENGTH = 50;

    private static final Log LOGGER = LogFactory.getLog(ClientNetworkCore.class);

    private final BlockingQueue<ClientMessage> retreiveMessageQueue;

    private final BlockingQueue<ClientMessage> sendMessageQueue;

    private boolean running = false;

    private BlockingQueue<ClientMessage> serverNetworkReceiveQueueFake;

    public ClientNetworkCore(BlockingQueue<ClientMessage> networkIncomingQueue, BlockingQueue<ClientMessage> networkOutgoingQueue,
        BlockingQueue<ClientMessage> serverNetworkReceiveQueueFake) {
        this.sendMessageQueue = networkIncomingQueue;
        this.retreiveMessageQueue = networkOutgoingQueue;
        this.serverNetworkReceiveQueueFake = serverNetworkReceiveQueueFake;
    }

    private void start() {
        LOGGER.debug("Starting network ...");
        running = true;
        while (running) {
            retreiveCoreMessages();
            retrieveServerMessages();
            try {
                Thread.sleep(TICK_LENGTH);
            } catch (InterruptedException e) {
                LOGGER.error("Error in client network:", e);
            }
        }
    }

    private void retrieveServerMessages() {
        while (!serverNetworkReceiveQueueFake.isEmpty()) {
            ClientMessage msg;
            try {
                msg = serverNetworkReceiveQueueFake.take();

                if (msg.getMessage().equals(MessageConstants.SHUTDOWN)) {
                    retreiveMessageQueue.offer(new ClientMessage(MessageConstants.SHUTDOWN));
                    shutdown();
                } else {
                    retreiveMessageQueue.offer(new ClientMessage(msg.getMessage(), msg.getData()));
                }

            } catch (InterruptedException e) {
                LOGGER.error("Error reading server messages: ", e);
            }
        }
    }

    private void retreiveCoreMessages() {
        while (!sendMessageQueue.isEmpty()) {
            ClientMessage msg;
            try {
                msg = sendMessageQueue.take();
                LOGGER.debug("Got new message for Server: " + msg);
            } catch (InterruptedException e) {
                LOGGER.error("Error reading core messages: ", e);
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
