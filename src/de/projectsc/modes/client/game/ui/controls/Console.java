/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.game.ui.controls;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import de.projectsc.core.data.KeyboardInputCommand;
import de.projectsc.core.data.MouseInputCommand;
import de.projectsc.core.interfaces.InputCommandListener;
import de.projectsc.modes.client.core.ui.UIElement;

/**
 * Chat implementation for ui.
 * 
 * @author Josch Bosch
 */
public class Console extends UIElement implements InputCommandListener {

    private final List<String> lines = new ArrayList<>();

    private String currentInput = "";

    public Console() {
        super("Console", 1);
        setVisible(false);
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
        if (command.getKey() == Keyboard.KEY_F1 && command.isKeyDown() && !command.isKeyRepeated()) {
            setVisible(!isVisible());
            command.consumed();
        }
        if (this.isVisible()) {
            if (command.isKeyDown() && !command.isKeyRepeated()) {
                if ((command.getKey() >= Keyboard.KEY_1 && command.getKey() <= Keyboard.KEY_0)) {
                    currentInput += command.getCharacter();
                }
                if ((command.getKey() >= Keyboard.KEY_Q && command.getKey() <= Keyboard.KEY_P)) {
                    if (command.isShiftDown()) {
                        currentInput += Character.toUpperCase(command.getCharacter());
                    } else {
                        currentInput += command.getCharacter();
                    }
                }
                if ((command.getKey() >= Keyboard.KEY_A && command.getKey() <= Keyboard.KEY_L)) {
                    if (command.isShiftDown()) {
                        currentInput += Character.toUpperCase(command.getCharacter());
                    } else {
                        currentInput += command.getCharacter();
                    }
                }
                if ((command.getKey() >= Keyboard.KEY_Z && command.getKey() <= Keyboard.KEY_M)) {
                    if (command.isShiftDown()) {
                        currentInput += Character.toUpperCase(command.getCharacter());
                    } else {
                        currentInput += command.getCharacter();
                    }
                }
                if (command.getKey() == Keyboard.KEY_RETURN) {
                    String newLine = currentInput;
                    lines.add(newLine);
                    currentInput = "";
                }
            }
            command.consumed();
        }

    }

    @Override
    public void handleMouseCommand(MouseInputCommand command) {
        // TODO Auto-generated method stub

    }
}
