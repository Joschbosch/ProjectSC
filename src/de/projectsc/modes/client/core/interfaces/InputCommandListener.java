/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.interfaces;

import de.projectsc.modes.client.core.data.KeyboardInputCommand;
import de.projectsc.modes.client.core.data.MouseInputCommand;

/**
 * Interface for all input listener.
 * 
 * @author Josch Bosch
 */
public interface InputCommandListener {

    /**
     * Level of consumption.
     * 
     * @author Josch Bosch
     */
    enum InputConsumeLevel {
        FIRST,
        SECOND
    };

    /**
     * @return {@link InputConsumeLevel}
     */
    InputConsumeLevel getInputConsumeLevel();

    /**
     * Handle a command for the keyboard.
     * 
     * @param command to handle
     */
    void handleKeyboardCommand(KeyboardInputCommand command);

    /**
     * Handle a command for the mouse.
     * 
     * @param command to handle
     */
    void handleMouseCommand(MouseInputCommand command);

}
