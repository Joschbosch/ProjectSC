/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.manager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import de.projectsc.core.data.InputCommand;
import de.projectsc.core.data.KeyboardInputCommand;
import de.projectsc.core.data.MouseInputCommand;
import de.projectsc.core.interfaces.InputCommandListener;

public class InputConsumeManager {

    private Map<InputCommandListener.InputConsumeLevel, List<InputCommandListener>> registered = new HashMap<>();

    public void addListener(InputCommandListener c) {
        if (c != null) {
            if (registered.get(c.getInputConsumeLevel()) == null) {
                List<InputCommandListener> newList = new LinkedList<>();
                registered.put(c.getInputConsumeLevel(), newList);
            }
            registered.get(c.getInputConsumeLevel()).add(c);
        }
    }

    public void removeListener(InputCommandListener toRemove) {
        if (registered.get(toRemove.getInputConsumeLevel()) != null) {
            registered.get(toRemove.getInputConsumeLevel()).remove(toRemove);
        }
    }

    public void processInput(Queue<InputCommand> readInput) {

        for (InputCommandListener.InputConsumeLevel level : InputCommandListener.InputConsumeLevel.values()) {
            if (registered.get(level) != null) {
                for (InputCommandListener listener : registered.get(level)) {
                    for (InputCommand command : readInput) {
                        if (command instanceof KeyboardInputCommand) {
                            listener.handleKeyboardCommand((KeyboardInputCommand) command);
                        }
                        if (command instanceof MouseInputCommand) {
                            listener.handleMouseCommand((MouseInputCommand) command);
                        }
                    }
                }
            }
        }
    }

}
