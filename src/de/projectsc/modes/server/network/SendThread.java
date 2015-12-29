/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.server.network;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.esotericsoftware.kryonet.Connection;

import de.projectsc.core.messages.NetworkMessage;
import de.projectsc.modes.server.core.data.AuthenticatedClient;
import de.projectsc.modes.server.core.messages.ServerMessage;

/**
 * Dirty implementation for testing.
 * 
 * @author Josch Bosch
 */
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
        LOGGER.debug("Starting send thread for " + client.getDisplayName());
        while (true) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(Inclusion.NON_NULL);
                ServerMessage message = client.getSendToClientQueue().take();
                NetworkMessage sendMessage = new NetworkMessage();
                sendMessage.setMsg(message.getMessage());
                sendMessage.setData(mapper.writeValueAsString(message.getData()));
                // System.x("Senging bytes : " + mapper.writeValueAsString(sendMessage).getBytes().length);
                connection.sendUDP(mapper.writeValueAsString(sendMessage));
            } catch (InterruptedException | IOException e) {
                LOGGER.error("Client thread interrupted", e);
            }
        }
    }
}
