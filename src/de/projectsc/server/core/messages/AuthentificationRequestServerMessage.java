/*
 * Copyright (C) 2015 
 */
 
package de.projectsc.server.core.messages;

import java.util.Map;

import com.esotericsoftware.kryonet.Connection;

/**
 * 
 * New {@link Client} sends authentification request to server.
 *
 * @author David Scholz
 */
public class AuthentificationRequestServerMessage implements ServerMessage {
    
    private final Connection client;
    
    private final Map<RequestEnum, String> request;
    
    public AuthentificationRequestServerMessage(Connection connection, Map<RequestEnum, String> request) {
        this.client = connection;
        this.request = request;
    }
    
    public Connection getClient() {
        return client;
    }
    
    public Map<RequestEnum, String>  getRequest() {
        return request;
    }

    @Override
    public Connection getConnection() {
        return client;
    }
    
}
