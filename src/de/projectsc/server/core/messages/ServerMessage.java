/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.server.core.messages;

import de.projectsc.core.data.messages.Message;

/**
 * Messages exchanged on the server internally.
 * 
 * @author Josch Bosch
 */
public class ServerMessage extends Message {

    public ServerMessage(String msg, Object... data) {
        super(msg, data);
    }

}
