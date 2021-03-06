/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.messages;

import de.projectsc.core.interfaces.Message;

/**
 * Base class for messages.
 * 
 * @author Josch Bosch
 */
public abstract class DefaultMessage implements Message {

    private final String message;

    private final Object[] data;

    public DefaultMessage(String msg, Object... data) {
        this.message = msg;
        this.data = data;
    }

    @Override
    public Object[] getData() {
        return data;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        String result = "";
        result += message + " ";
        if (data != null) {
            result += "Attribut : ";
            result += data;
        }
        return result;
    }
}
