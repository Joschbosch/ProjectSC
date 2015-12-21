/*
 * Copyright (C) 2015
 */

package de.projectsc.core.interfaces;

/**
 * Interface for all kinds of messages.
 * 
 * @author Joshc Bosch
 */
public interface Message {

    /**
     * @return attached data
     */
    Object[] getData();

    /**
     * @return the message
     */
    String getMessage();
}
