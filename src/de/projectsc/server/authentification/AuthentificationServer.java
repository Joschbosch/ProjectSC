/*
 * Project SC-2015 
 */
 
package de.projectsc.server.authentification;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.server.authentification.api.AuthentificationReader;
import de.projectsc.server.authentification.api.AuthentificationReader.Line;
import de.projectsc.server.authentification.database.DatabaseConnection;
import de.projectsc.server.authentification.impl.reader.CSVAuthentificationReader;
import de.projectsc.server.core.messages.AuthentificationRequestServerMessage;
import de.projectsc.server.core.messages.AuthentificationResponseServerMessage;
import de.projectsc.server.core.messages.RequestEnum;
import de.projectsc.server.core.messages.ServerMessage;

/**
 * Handles {@link AuthentificationRequestServerMessage}s.
 *
 * @author David Scholz
 */
public class AuthentificationServer {
    
    private static final String PATH = "";
    
    private static final Log LOGGER = LogFactory.getLog(DatabaseConnection.class);
    
    private final BlockingQueue<ServerMessage> authentificationQueue;
    
    public AuthentificationServer() {
        this.authentificationQueue = new LinkedBlockingQueue<>();
    }
    
    public void handleAuthentificationRequests() {
        ServerMessage msg = authentificationQueue.poll();
        
        if (msg instanceof AuthentificationRequestServerMessage) {
            AuthentificationRequestServerMessage authetificationMsg = (AuthentificationRequestServerMessage) msg;
            Map<RequestEnum, String> requestMap = authetificationMsg.getRequest();
            try {
                AuthentificationReader reader = new CSVAuthentificationReader(new FileReader(new File(PATH)), null);
                Line l = reader.next();
                ServerMessage response = null;
                if (l.get("Name").equals(requestMap.get(RequestEnum.NAME)) && 
                    l.get("Email").equals(requestMap.get(RequestEnum.EMAIL)) && 
                    l.get("Password").equals(requestMap.get(RequestEnum.PASSWORD))) {
                    response = new AuthentificationResponseServerMessage(authetificationMsg.getClient(), true);
                } else {
                    response = new AuthentificationResponseServerMessage(authetificationMsg.getClient(), false);
                }
                authentificationQueue.put(response);
            } catch (IOException e) {
                LOGGER.error(e);
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
        }
    }
    
    public BlockingQueue<ServerMessage> getAuthentificationQueue() {
        return authentificationQueue;
    }

}
