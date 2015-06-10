/*
 * Copyright (C) 2006-2015 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.projectsc.core.data;

public class Message {

    private final String message;

    private final Object data;

    public Message(String msg, Object data) {
        this.message = msg;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

}
