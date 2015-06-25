/*
 * Copyright (C) 2015 
 */

package de.projectsc.server.core;

import de.projectsc.core.data.messages.Message;

public class ServerMessage extends Message {

    public ServerMessage(String msg, Object data) {
        super(msg, data);
    }

}
