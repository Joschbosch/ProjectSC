/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.client.core.messages;

import de.projectsc.core.data.messages.Message;

/**
 * Message type that is exchanged between client and it's network.
 * 
 * @author Josch Bosch
 */
public class ClientMessage extends Message {

    public ClientMessage(String msg, Object... data) {
        super(msg, data);
    }

}
