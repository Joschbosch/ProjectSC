/*
 * Copyright (C) 2015 
 */
 
package de.projectsc.server.network;

import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import de.projectsc.server.core.ServerCore;
import de.projectsc.server.core.messages.ServerMessage;
import de.projectsc.server.network.utils.ServerNetworkUtils;

/**
 * Network Server.
 *
 * @author David Scholz
 */
public class ServerNetworkCore {
	
    private static final Log LOGGER = LogFactory.getLog(ServerNetworkCore.class);
    
	private ServerCore serverCore;
	
	private BlockingQueue<ServerMessage> coreQueue;
	
	public ServerNetworkCore(ServerCore serverCore, BlockingQueue<ServerMessage> coreQueue) {
			this.serverCore = serverCore;
			this.coreQueue = coreQueue;
			Server server = new Server();
			server.addListener(new ClientListener(coreQueue));
			ServerNetworkUtils.register(server);
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
    
    public ClientListener(BlockingQueue<ServerMessage> coreQueue) {
        this.coreQueue = coreQueue;
    }
    
    @Override
    public void connected(Connection client) {
        super.connected(client);
        
    }
    
    @Override
    public void disconnected(Connection client) {
        super.disconnected(client);
        
    }
    
    @Override
    public void received(Connection client, Object object) {
        super.received(client, object);
        // this is necessary since kryonet sends heartbeats.
        if (!(object instanceof FrameworkMessage)) {
            
        }
        
    }
    
}
