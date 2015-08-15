/*
 * 
 * Project SC - 2015
 * 
 * 
 */
 
package de.projectsc.server.core.messages;

import com.esotericsoftware.kryonet.Connection;

/**
 * Basis for internal server messages.
 *
 * @author David Scholz
 */
public abstract class ServerMessage implements Message {
    
    public ServerMessage() {}

    public abstract Connection getConnection();
}
