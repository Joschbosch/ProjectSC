/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.server.core.messages;

import de.projectsc.core.messages.DefaultMessage;

/**
 * Messages exchanged on the server internally.
 * 
 * @author Josch Bosch
 */
public class ServerMessage extends DefaultMessage {

    public ServerMessage(String msg, Object... data) {
        super(msg, data);
    }

}
