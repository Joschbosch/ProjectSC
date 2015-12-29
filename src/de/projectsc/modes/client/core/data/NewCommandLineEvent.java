/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.data;

import de.projectsc.core.data.Event;

public class NewCommandLineEvent extends Event {

    public static final String ID = "NewCommandLineEvent";

    private String command;

    public NewCommandLineEvent(String command) {
        super(ID);
        this.setCommand(command);
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

}
