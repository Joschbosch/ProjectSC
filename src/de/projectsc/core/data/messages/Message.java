/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.core.data.messages;

/**
 * Base class for messages.
 * 
 * @author Josch Bosch
 */
public abstract class Message {

    private final String message;

    private final Object[] data;

    public Message(String msg, Object... data) {
        this.message = msg;
        this.data = data;
    }

    public Object[] getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        String result = "";
        result += message + " ";
        if (data != null) {
            for (Object o : data) {
                result += "Attribut : ";
                result += o.toString() + ";   ";
            }
        }
        return result;
    }
}
