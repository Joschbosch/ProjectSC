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
import de.projectsc.core.data.messages.NetworkMessage;

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

    private final BlockingQueue<NetworkMessage> serverNetworkSendQueueFake;

    private final BlockingQueue<NetworkMessage> serverNetworkReceiveQueueFake;

    public ClientNetworkCore(BlockingQueue<ClientMessage> networkIncomingQueue, BlockingQueue<ClientMessage> networkOutgoingQueue,
        BlockingQueue<NetworkMessage> blockingQueue, BlockingQueue<NetworkMessage> blockingQueue2) {
        this.sendMessageQueue = networkIncomingQueue;
        this.retreiveMessageQueue = networkOutgoingQueue;
        this.serverNetworkReceiveQueueFake = blockingQueue;
        this.serverNetworkSendQueueFake = blockingQueue2;
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
            NetworkMessage msg;
            try {
                msg = serverNetworkReceiveQueueFake.take();

                if (msg.getMessage().equals(MessageConstants.SHUTDOWN)) {
                    retreiveMessageQueue.offer(new ClientMessage(MessageConstants.SHUTDOWN, null));
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
                if (MessageConstants.SHUTDOWN.equals(msg.getMessage())) {
                    serverNetworkSendQueueFake.offer(new NetworkMessage(msg.getMessage(), msg.getData()));
                    shutdown();
                } else {
                    serverNetworkSendQueueFake.offer(new NetworkMessage(msg.getMessage(), msg.getData()));
                }
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
