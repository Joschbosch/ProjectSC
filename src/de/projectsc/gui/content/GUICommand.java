/*
 * Copyright (C) 2015 
 */

package de.projectsc.gui.content;

public class GUICommand {

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
