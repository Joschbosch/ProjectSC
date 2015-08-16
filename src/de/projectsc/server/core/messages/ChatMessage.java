/*
 * Project SC-2015 
 */
 
package de.projectsc.server.core.messages;

import com.esotericsoftware.kryonet.Connection;

/**
 * 
 * {@link ServerMessage} for chat messages.
 *
 * @author David Scholz
 */
public class ChatMessage implements ServerMessage {

    private Connection client;
    
    private String msg;
    
    public ChatMessage(Connection client, String msg) {
        this.client = client;
        this.msg = msg;
    }
    
    @Override
    public Connection getConnection() {
        return client;
    }

}
