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
import de.projectsc.server.core.ServerConstants;
import de.projectsc.server.core.ServerCore;
import de.projectsc.server.core.serverMessages.ServerMessage;

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

    private final BlockingQueue<ServerMessage> coreQueue;

    private ServerCore serverCore;

    public ServerNetworkCore(ServerCore serverCore, BlockingQueue<ServerMessage> coreQueue) {
        this.coreQueue = coreQueue;
        clientReceiveQueueFaking = new LinkedBlockingQueue<>();
        this.serverCore = serverCore;
    }

    private void start() {
        LOGGER.debug("Starting network ...");
        running = true;
        while (running) {
            retreiveClientMessages();
            running = serverCore.isAlive();
            try {
                Thread.sleep(ServerConstants.SLEEPTIME);
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
                    if (msg.getMessage().equals(MessageConstants.SHUTDOWN)) {
                        coreQueue.offer(new ServerMessage(msg.getMessage(), msg.getData()));
                        shutdown();
                    } else {
                        coreQueue.offer(new ServerMessage(msg.getMessage(), msg.getData()));
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.error("Error reading network messages: ", e);
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
