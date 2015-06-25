/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.gui;

import de.projectsc.core.data.messages.Message;

/**
 * Implementation of {@link Message} for the GUI.
 * 
 * @author Josch Bosch
 */
public class GUIMessage extends Message {

    public GUIMessage(String msg, Object data) {
        super(msg, data);
    }

}
