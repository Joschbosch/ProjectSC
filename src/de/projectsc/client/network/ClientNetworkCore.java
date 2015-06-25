/*
 * Copyright (C) 2015
 */

package de.projectsc.client.network;

import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.client.core.ClientMessage;
import de.projectsc.client.core.ClientMessageConstants;
import de.projectsc.client.gui.GUIMessageConstants;
import de.projectsc.core.data.messages.MessageConstants;
import de.projectsc.core.data.messages.NetworkMessage;
import de.projectsc.core.data.messages.NetworkMessageConstants;

public class ClientNetworkCore implements Runnable {

    private static final Log LOGGER = LogFactory.getLog(ClientNetworkCore.class);

    private final BlockingQueue<ClientMessage> retreiveMessageQueue;

    private final BlockingQueue<ClientMessage> sendMessageQueue;

    private boolean running = false;

    private BlockingQueue<NetworkMessage> serverNetworkSendQueueFake;

    private BlockingQueue<NetworkMessage> serverNetworkReceiveQueueFake;

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
        retreiveMessageQueue.offer(new ClientMessage(ClientMessageConstants.CLIENT_READY, null));
        while (running) {
            retreiveCoreMessages();
            retrieveServerMessages();
            try {
                Thread.sleep(10);
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

                if (msg.getMessage().equals(MessageConstants.CLOSE_DOWN)) {
                    retreiveMessageQueue.offer(new ClientMessage(MessageConstants.CLOSE_DOWN, null));
                    shutdown();
                } else if (msg.getMessage().equals(NetworkMessageConstants.INITIALIZE_GAME)) {
                    LOGGER.debug("Client receiving message: " + msg.getMessage());
                    retreiveMessageQueue.offer(new ClientMessage(GUIMessageConstants.INIT_GAME, msg.getData()));
                } else if (msg.getMessage().equals(NetworkMessageConstants.NEW_LOCATION)) {
                    retreiveMessageQueue.offer(new ClientMessage(NetworkMessageConstants.NEW_LOCATION, msg.getData()));
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
                if (MessageConstants.CLOSE_DOWN.equals(msg.getMessage())) {
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
