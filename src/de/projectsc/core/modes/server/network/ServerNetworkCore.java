/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.modes.server.network;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;

import de.projectsc.core.data.messages.MessageConstants;
import de.projectsc.core.data.messages.NetworkMessage;
import de.projectsc.core.modes.server.core.ServerCore;
import de.projectsc.core.modes.server.core.data.AuthenticatedClient;
import de.projectsc.core.modes.server.core.data.connections.messages.NewClientConnectedServerMessage;
import de.projectsc.core.modes.server.core.messages.ServerMessage;

/**
 * Core class for server network.
 * 
 * @author Josch Bosch
 */
public class ServerNetworkCore {

    private static final Log LOGGER = LogFactory.getLog(ServerNetworkCore.class);

    public ServerNetworkCore(ServerCore serverCore, BlockingQueue<ServerMessage> coreQueue) {
        Server server = new Server();
        new Thread(server).start();
        try {
            server.bind(54555, 54777);
        } catch (IOException e1) {
            LOGGER.error("Could not open server", e1);
        }
        server.addListener(new ClientConnectedListener(coreQueue));
        Kryo kryo = server.getKryo();
        kryo.register(ServerMessage.class);
        kryo.register(Message.class);
        kryo.register(String.class);
        kryo.register(Object[].class);
        kryo.register(Object.class);
    }
}

/**
 * Dirty implementation for testing.
 * 
 * @author Josch Bosch
 */
class ClientConnectedListener extends Listener {

    private static final Log LOGGER = LogFactory.getLog(ClientConnectedListener.class);

    private final BlockingQueue<ServerMessage> coreQueue;

    private AuthenticatedClient newClient;

    ClientConnectedListener(BlockingQueue<ServerMessage> coreQueue) {
        this.coreQueue = coreQueue;
    }

    @Override
    public void connected(Connection client) {
        super.connected(client);
        BlockingQueue<ServerMessage> sendQueue = new LinkedBlockingQueue<>();
        BlockingQueue<ServerMessage> receiveQueue = new LinkedBlockingQueue<>();
        newClient = new AuthenticatedClient(0, "Josch", sendQueue, receiveQueue);
        SendThread thread = new SendThread(client, newClient);
        new Thread(thread).start();
        coreQueue.add(new NewClientConnectedServerMessage(MessageConstants.NEW_CLIENT_CONNECTED, newClient));
        LOGGER.debug(String.format("New connection accepted: User %s ID %d", newClient.getDisplayName(), newClient.getId()));
    }

    @Override
    public void received(Connection arg0, Object arg1) {
        super.received(arg0, arg1);
        if (!(arg1 instanceof FrameworkMessage)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                NetworkMessage msg = mapper.readValue((String) arg1, NetworkMessage.class);
                newClient.received(new ServerMessage(msg.getMsg(), msg.getData()));
            } catch (IOException e) {
                LOGGER.error(e.getStackTrace());
            }
        }
    }

}
