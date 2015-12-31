/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.input;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import de.projectsc.modes.client.core.data.InputCommand;
import de.projectsc.modes.client.core.data.KeyboardInputCommand;
import de.projectsc.modes.client.core.data.MouseInputCommand;
import de.projectsc.modes.client.core.interfaces.InputCommandListener;

/**
 * Manager for consuming the input.
 * 
 * @author Josch Bosch
 */
public class InputConsumeManager {

    private static InputConsumeManager instance;

    private Map<InputCommandListener.InputConsumeLevel, List<InputCommandListener>> registered = new HashMap<>();

    public InputConsumeManager() {
        setInstance(this);
    }

    /**
     * Add a new listener to consume input.
     * 
     * @param c to add
     */
    public void addListener(InputCommandListener c) {
        if (c != null) {
            if (registered.get(c.getInputConsumeLevel()) == null) {
                List<InputCommandListener> newList = new LinkedList<>();
                registered.put(c.getInputConsumeLevel(), newList);
            }
            registered.get(c.getInputConsumeLevel()).add(c);
        }
    }

    /**
     * Remove listener.
     * 
     * @param toRemove listener
     */
    public void removeListener(InputCommandListener toRemove) {
        if (registered.get(toRemove.getInputConsumeLevel()) != null) {
            registered.get(toRemove.getInputConsumeLevel()).remove(toRemove);
        }
    }

    /**
     * Process all input.
     * 
     * @param readInput the input to process.
     */
    public void processInput(Queue<InputCommand> readInput) {
        for (InputCommandListener.InputConsumeLevel level : InputCommandListener.InputConsumeLevel.values()) {
            if (registered.get(level) != null) {
                for (InputCommand command : readInput) {
                    for (InputCommandListener listener : registered.get(level)) {
                        if (!command.isConsumed()) {
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

    public static InputConsumeManager getInstance() {
        return instance;
    }

    public static void setInstance(InputConsumeManager instance) {
        InputConsumeManager.instance = instance;
    }
}
