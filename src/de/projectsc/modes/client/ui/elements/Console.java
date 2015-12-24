/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.ui.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import de.projectsc.core.data.KeyboardInputCommand;
import de.projectsc.core.data.MouseInputCommand;
import de.projectsc.core.interfaces.InputCommandListener;
import de.projectsc.modes.client.core.states.UIElementConstants;
import de.projectsc.modes.client.ui.BasicUIElement;

/**
 * Chat implementation for ui.
 * 
 * @author Josch Bosch
 */
public class Console extends BasicUIElement implements InputCommandListener {

    private final List<String> lines = new ArrayList<>();

    private String currentInput = "";

    public Console() {
        super(UIElementConstants.CONSOLE, 1);
        lines.add("This is a test line 1");
        lines.add("This is a test line 2");
        lines.add("This is a test line 3");
        lines.add("This is a test line 4");
        lines.add("This is a test line 5");
        lines.add("This is a test line 6");
        lines.add("This is a test line 1");
        lines.add("This is a test line 2");
        lines.add("This is a test line 3");
        lines.add("This is a test line 4");
        lines.add("This is a test line 5");
        lines.add("This is a test line 6");
    }

    /**
     * New line.
     * 
     * @param line to add
     */
    public void addLine(String line) {
        lines.add(line);
    }

    public List<String> getLines() {
        return lines;
    }

    @Override
    public void handleInput(Map<Integer, Integer> keyMap) {
        if (keyMap.get(Keyboard.KEY_TAB) != null
            && keyMap.get(Keyboard.KEY_TAB) == 1) {
            setVisible(!isVisible());
        } else if (isVisible()) {
            for (Integer i : keyMap.keySet()) {
                if (keyMap.get(i) == 1) {
                    currentInput += Keyboard.getKeyName(i);
                }
            }
        }
    }

    public String getCurrentInput() {
        return currentInput;
    }

    public void setCurrentInput(String currentInput) {
        this.currentInput = currentInput;
    }

    @Override
    public InputConsumeLevel getInputConsumeLevel() {
        return InputConsumeLevel.FIRST;
    }

    @Override
    public void handleKeyboardCommand(KeyboardInputCommand command) {
        if (command.getKey() == Keyboard.KEY_F1) {
            setVisible(!isVisible());
        }
    }

    @Override
    public void handleMouseCommand(MouseInputCommand command) {
        // TODO Auto-generated method stub

    }
}
