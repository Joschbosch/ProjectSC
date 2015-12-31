/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.data;

import de.projectsc.core.data.Event;

/**
 * Event for a new command.
 * 
 * @author Josch Bosch
 */
public class NewCommandLineEvent extends Event {

    /**
     * ID.
     */
    public static final String ID = NewCommandLineEvent.class.getName();

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
