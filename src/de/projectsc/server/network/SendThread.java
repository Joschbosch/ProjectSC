/*
 * Copyright (C) 2015 
 */
 
package de.projectsc.server.network;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esotericsoftware.kryonet.Connection;

import de.projectsc.core.data.messages.NetworkMessage;
import de.projectsc.server.core.client.Client;
import de.projectsc.server.core.messages.ServerMessage;

public class SendThread implements Runnable {
    
    private static final Log LOGGER = LogFactory.getLog(SendThread.class);
    
    private final Client client;
    
    private final Connection connection;
    
    public SendThread(Client client, Connection connection) {
        this.client = client;
        this.connection = connection;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                ServerMessage message = client.getSendToClientQueue().take();
                NetworkMessage sendMessage = new NetworkMessage();
            } catch (InterruptedException e) {
                LOGGER.error("Client thread interrupted", e);
            }
        }
        
    }

}
