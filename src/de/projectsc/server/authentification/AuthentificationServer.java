/*
 * Project SC-2015 
 */
 
package de.projectsc.server.authentification;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import de.projectsc.server.core.messages.AuthentificationRequestServerMessage;
import de.projectsc.server.core.messages.ServerMessage;

/**
 * Handles {@link AuthentificationRequestServerMessage}s.
 *
 * @author David Scholz
 */
public class AuthentificationServer {
    
    private final BlockingQueue<ServerMessage> authentificationQueue;
    
    public AuthentificationServer() {
        this.authentificationQueue = new LinkedBlockingQueue<>();
    }
    
    public BlockingQueue<ServerMessage> getAuthentificationQueue() {
        return authentificationQueue;
    }

}
