/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.interfaces;

import de.projectsc.core.data.KeyboardInputCommand;
import de.projectsc.core.data.MouseInputCommand;

public interface InputCommandListener {

    enum InputConsumeLevel {
        FIRST,
        SECOND
    };

    InputConsumeLevel getInputConsumeLevel();

    void handleKeyboardCommand(KeyboardInputCommand command);

    void handleMouseCommand(MouseInputCommand command);

}
