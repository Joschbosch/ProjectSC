/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.messages;

import de.projectsc.core.messages.DefaultMessage;

/**
 * Message type that is exchanged between client and it's network.
 * 
 * @author Josch Bosch
 */
public class ClientMessage extends DefaultMessage {

    public ClientMessage(String msg, Object... data) {
        super(msg, data);
    }

}
