/*
 * Project SC-2015 
 */
 
package de.projectsc.server.core.messages;

import com.esotericsoftware.kryonet.Connection;

/**
 * Server message.
 *
 * @author David Scholz
 */
public interface ServerMessage {
    
    Connection getConnection();
}
