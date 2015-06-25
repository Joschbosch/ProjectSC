/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.gui.content;

/**
 * Commands that are only used in the GUI to communicate.
 * 
 * @author Josch Bosch
 */
public class GUICommand {

    /**
     * Message type.
     */
    public static final String CHANGE_LOCATION = "Change View Location";

    private final String message;

    private final Object data;

    public GUICommand(String msg, Object data) {
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
