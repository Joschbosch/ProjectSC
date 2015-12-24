/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.network;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

import de.projectsc.core.messages.MessageConstants;
import de.projectsc.core.messages.NetworkMessage;
import de.projectsc.modes.client.messages.ClientMessage;

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

    private final boolean connected = false;

    private final Client client;

    public ClientNetworkCore(BlockingQueue<ClientMessage> networkIncomingQueue, BlockingQueue<ClientMessage> networkOutgoingQueue,
        BlockingQueue<ClientMessage> serverNetworkReceiveQueueFake) {
        this.sendMessageQueue = networkIncomingQueue;
        this.retreiveMessageQueue = networkOutgoingQueue;
        client = new Client();
        new Thread(client).start();
    }

    private void start() {
        LOGGER.debug("Starting network ...");
        running = true;
        while (running) {
            retreiveCoreMessages();
            try {
                Thread.sleep(TICK_LENGTH);
            } catch (InterruptedException e) {
                LOGGER.error("Error in client network:", e);
            }
        }
    }

    private void retreiveCoreMessages() {
        while (!sendMessageQueue.isEmpty()) {
            ClientMessage msg;
            try {
                msg = sendMessageQueue.take();
                LOGGER.debug("Got new message for Server: " + msg);
                if (msg.getMessage().equals(MessageConstants.CONNECT)) {
                    connectToServer();
                } else {
                    NetworkMessage sendMessage = new NetworkMessage();
                    sendMessage.setMsg(msg.getMessage());
                    sendMessage.setData(msg.getData());
                    ObjectMapper mapper = new ObjectMapper();
                    client.sendUDP(mapper.writeValueAsString(sendMessage));
                }
            } catch (InterruptedException | IOException e) {
                LOGGER.error("Error reading core messages: ", e);
            }
        }

    }

    private void connectToServer() {
        LOGGER.debug("Connecting to server ...");
        try {
            client.connect(5000, "localhost", 54555, 54777);
            Kryo kryo = client.getKryo();
            kryo.register(ClientMessage.class);
            kryo.register(Message.class);
            kryo.register(String.class);
            kryo.register(Object[].class);
            kryo.register(Object.class);
            client.addListener(new Listener() {

                @Override
                public void received(Connection connection, Object object) {
                    if (!(object instanceof FrameworkMessage)) {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            NetworkMessage msg = mapper.readValue((String) object, NetworkMessage.class);
                            retreiveMessageQueue.offer(new ClientMessage(msg.getMsg(), msg.getData()));
                        } catch (IOException e) {
                            LOGGER.error(e.getStackTrace());
                        }
                    }
                }

            });
            LOGGER.debug("Client connection established");
        } catch (IOException e) {
            LOGGER.error("Could not connect: " + e);
        }
    }

    @Override
    public void run() {
        start();
    }

}
