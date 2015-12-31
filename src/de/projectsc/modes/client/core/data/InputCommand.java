/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.data;

/**
 * Interface for all input commands.
 * 
 * @author Josch Bosch
 */
public interface InputCommand {

    /**
     * @return true, if the command is consumed.
     */
    boolean isConsumed();

    /**
     * Consume command.
     */
    void consume();
}
