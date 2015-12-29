/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.data;

import de.projectsc.core.messages.DefaultMessage;

public class ClientInputRequest extends DefaultMessage {

    public ClientInputRequest(String msg, long tickNumber, Object[] data) {
        super(msg, data);
    }

}
