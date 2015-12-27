/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.interfaces;

import de.projectsc.modes.client.core.data.KeyboardInputCommand;
import de.projectsc.modes.client.core.data.MouseInputCommand;

public interface InputCommandListener {

    enum InputConsumeLevel {
        FIRST,
        SECOND
    };

    InputConsumeLevel getInputConsumeLevel();

    void handleKeyboardCommand(KeyboardInputCommand command);

    void handleMouseCommand(MouseInputCommand command);

}
