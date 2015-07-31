/*
 * Copyright (C) 2015 
 */
 
package de.projectsc.server.network.utils;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Server;

import de.projectsc.server.core.messages.NewAuthenticatedClientServerMessage;
import de.projectsc.server.core.messages.NewClientConnectedServerMessage;
import de.projectsc.server.core.messages.ServerMessage;
import de.projectsc.server.network.ServerNetworkCore;

/**
 * Utility class for {@link ServerNetworkCore}
 *
 * @author David Scholz
 */
public final class ServerNetworkUtils {
    
    private static final Log LOGGER = LogFactory.getLog(ServerNetworkUtils.class);
    
    private static final int TCP_PORT = 54555;
    
    private static final int UDP_PORT =  54777;
  
    private ServerNetworkUtils() {}
    
    public static void register(EndPoint endpoint) {
        Kryo kryo = endpoint.getKryo();

        kryo.register(NewClientConnectedServerMessage.class);
        kryo.register(NewAuthenticatedClientServerMessage.class); 
        kryo.register(ServerMessage.class);
      
        if (endpoint instanceof Server) {
            try {
                ((Server) endpoint).bind(TCP_PORT, UDP_PORT);
            } catch (IOException e) {
                LOGGER.error("Failed to bind ports...", e);
            }
        }
        
        }
    }


