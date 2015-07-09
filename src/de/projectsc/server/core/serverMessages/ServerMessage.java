/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.server.core.serverMessages;

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
