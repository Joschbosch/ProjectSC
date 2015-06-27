/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.server.network;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.data.messages.MessageConstants;
import de.projectsc.core.data.messages.NetworkMessage;
import de.projectsc.server.core.ServerMessage;

/**
 * Core class for server network.
 * 
 * @author Josch Bosch
 */
public class ServerNetworkCore implements Runnable {

    private static final Log LOGGER = LogFactory.getLog(ServerNetworkCore.class);

    /**
     * FAKE! Until network is there.
     */
    public BlockingQueue<NetworkMessage> clientSendQueueFaking;

    /**
     * FAKE! Until network is there.
     */
    public BlockingQueue<NetworkMessage> clientReceiveQueueFaking;

    private boolean running = false;

    private BlockingQueue<ServerMessage> sendQueue;

    private BlockingQueue<ServerMessage> receiveQueue;

    public ServerNetworkCore(BlockingQueue<ServerMessage> sendQueue, BlockingQueue<ServerMessage> receiveQueue) {
        this.sendQueue = sendQueue;
        this.receiveQueue = receiveQueue;
        clientReceiveQueueFaking = new LinkedBlockingQueue<>();
        clientSendQueueFaking = new LinkedBlockingQueue<>();
    }

    private void sendMessage(ServerMessage msg) {
        clientSendQueueFaking.offer(new NetworkMessage(msg.getMessage(), msg.getData()));
    }

    private void start() {
        LOGGER.debug("Starting network ...");
        running = true;
        while (running) {
            retreiveCoreMessages();
            retreiveClientMessages();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                LOGGER.error("Server network error: ", e);
            }
        }
    }

    private void retreiveClientMessages() {
        while (!clientReceiveQueueFaking.isEmpty()) {
            NetworkMessage msg;
            try {
                msg = clientReceiveQueueFaking.take();

                if (msg != null) {
                    if (msg.getMessage().equals(MessageConstants.CLOSE_DOWN)) {
                        receiveQueue.offer(new ServerMessage(msg.getMessage(), msg.getData()));
                        shutdown();
                    } else {
                        receiveQueue.offer(new ServerMessage(msg.getMessage(), msg.getData()));
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.error("Error reading network messages: ", e);
            }
        }
    }

    private void retreiveCoreMessages() {
        while (!sendQueue.isEmpty()) {
            ServerMessage msg;
            try {
                msg = sendQueue.take();
                sendMessage(msg);
            } catch (InterruptedException e) {
                LOGGER.error("Error reading core messages: ", e);
            }
        }
    }

    private void shutdown() {
        LOGGER.debug("Shutting down server network");
        running = false;
    }

    @Override
    public void run() {
        start();
    }

}
