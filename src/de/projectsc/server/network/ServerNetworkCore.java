/*
 * Copyright (C) 2015 
 */
 
package de.projectsc.server.network;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import de.projectsc.server.core.ServerCore;
import de.projectsc.server.core.client.AuthenticatedClient;
import de.projectsc.server.core.client.Client;
import de.projectsc.server.core.messages.AuthentificationRequestServerMessage;
import de.projectsc.server.core.messages.AuthentificationResponseServerMessage;
import de.projectsc.server.core.messages.ClientDisconnectedServerMessage;
import de.projectsc.server.core.messages.NewClientConnectedServerMessage;
import de.projectsc.server.core.messages.ServerMessage;
import de.projectsc.server.network.utils.ServerNetworkUtils;

/**
 * Network Server.
 *
 * @author David Scholz
 */
public class ServerNetworkCore {
	
    private static final Log LOGGER = LogFactory.getLog(ServerNetworkCore.class);
    
	private final ServerCore serverCore;
	
	private final BlockingQueue<ServerMessage> coreQueue;
	
	public ServerNetworkCore(ServerCore serverCore, BlockingQueue<ServerMessage> coreQueue) {
			this.serverCore = serverCore;
			this.coreQueue = coreQueue;
			Server server = new Server();
			LOGGER.debug("Server started...");
			new Thread(server).start();
			server.addListener(new ClientListener(coreQueue));
			ServerNetworkUtils.register(server);
	}
	
	public ServerCore getServerCore() {
	    return serverCore;
	}
	
	public BlockingQueue<ServerMessage> getCoreQueue() {
	    return coreQueue;
	}

}

/**
 * Listener class which listens for client messages.
 *
 * @author David Scholz
 */
class ClientListener extends Listener {
    
    private static final Log LOGGER = LogFactory.getLog(ClientListener.class);
    
    private final BlockingQueue<ServerMessage> coreQueue;
    
    private Map<Client, Thread> clientToSendThreadMap = new HashMap<>();
    
    private Client newClient;
    
    public ClientListener(BlockingQueue<ServerMessage> coreQueue) {
        this.coreQueue = coreQueue;
    }
    
    @Override
    public void connected(Connection client) {
        super.connected(client);
        coreQueue.add(new AuthentificationRequestServerMessage(client));
    }
    
    @Override
    public void disconnected(Connection client) {
        super.disconnected(client);
        coreQueue.add(new ClientDisconnectedServerMessage());
        clientToSendThreadMap.get(client).interrupt();
    }
    
    @Override
    public void received(Connection client, Object object) {
        super.received(client, object);
        
        if (object instanceof AuthentificationResponseServerMessage) {
            if (((AuthentificationResponseServerMessage) object).isValid()) {
                BlockingQueue<ServerMessage> sendQueue = new LinkedBlockingQueue<>();
                BlockingQueue<ServerMessage> receiveQueue = new LinkedBlockingQueue<>();
                client.setName("Josch");
                newClient = new AuthenticatedClient("Josch", client.getID(), sendQueue, receiveQueue);
                SendThread runnable = new SendThread(newClient, client);
                Thread thread = new Thread(runnable);
                clientToSendThreadMap.put(newClient, thread);
                thread.start();
                coreQueue.add(new NewClientConnectedServerMessage());
                LOGGER.debug(String.format("New connection accepted: User %s ID %d", newClient.getDisplayName(), newClient.getId()));
            }            
        }
        
    }
    
}
