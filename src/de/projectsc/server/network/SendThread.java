/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.server.network;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

import com.esotericsoftware.kryonet.Connection;

import de.projectsc.core.data.messages.NetworkMessage;
import de.projectsc.server.core.AuthenticatedClient;
import de.projectsc.server.core.messages.ServerMessage;

public class SendThread implements Runnable {

    private static final Log LOGGER = LogFactory.getLog(SendThread.class);

    private final AuthenticatedClient client;

    private final Connection connection;

    public SendThread(Connection connection, AuthenticatedClient newClient) {
        this.connection = connection;
        this.client = newClient;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                ServerMessage message = client.getSendToClientQueue().take();
                NetworkMessage sendMessage = new NetworkMessage();
                sendMessage.setMsg(message.getMessage());
                sendMessage.setData(mapper.writeValueAsString(message.getData()));
                LOGGER.debug("Server sends message to client: " + mapper.writeValueAsString(sendMessage));
                connection.sendUDP(mapper.writeValueAsString(sendMessage));
            } catch (InterruptedException | IOException e) {
                LOGGER.error("Client thread interrupted", e);
            }
        }
    }
}
