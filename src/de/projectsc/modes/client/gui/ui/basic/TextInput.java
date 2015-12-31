/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.ui.basic;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import de.projectsc.modes.client.core.data.KeyboardInputCommand;
import de.projectsc.modes.client.core.data.MouseInputCommand;
import de.projectsc.modes.client.core.interfaces.InputCommandListener;

/**
 * Label with text input listener.
 * 
 * @author Josch Bosch
 */
public class TextInput extends Label implements InputCommandListener {

    private List<TextInputConsumer> consumer = new LinkedList<>();

    private String currentInput;

    private String prefix = "";

    private String suffix = "";

    private boolean consumeEmpty = false;

    public TextInput(Container c, Vector2f position) {
        super(c, position);
        this.currentInput = "";
        inputConsumeManager.addListener(this);
    }

    @Override
    public void handleKeyboardCommand(KeyboardInputCommand command) {
        if (isVisible() && isActive()) {
            if (command.isKeyDown() && !command.isKeyRepeated()) {
                if ((command.getKey() >= Keyboard.KEY_1 && command.getKey() <= Keyboard.KEY_0) && !command.isShiftDown()) {
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

                if (command.getKey() == Keyboard.KEY_RETURN
                    && !((currentInput.isEmpty() || currentInput.replaceAll(" ", "").isEmpty()) && !consumeEmpty)) {
                    String newLine = currentInput;
                    for (TextInputConsumer c : consumer) {
                        c.consumeText(newLine);
                    }
                    currentInput = "";
                }
            }

            if ((command.getKey() == Keyboard.KEY_SPACE) && !command.isControlDown()
                && !command.isShiftDown() && command.isKeyDown()) {
                currentInput += command.getCharacter();
            }
            if (command.isKeyDown() && command.getKey() == Keyboard.KEY_BACK && currentInput.length() > 0) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
            }
            setText(prefix + currentInput + suffix);
            command.consume();
        }
    }

    @Override
    public InputConsumeLevel getInputConsumeLevel() {
        return InputConsumeLevel.SECOND;
    }

    @Override
    public void handleMouseCommand(MouseInputCommand command) {

    }

    /**
     * Add new input consumer.
     * 
     * @param newConsumer to add
     */
    public void addTextInputConsumer(TextInputConsumer newConsumer) {
        this.consumer.add(newConsumer);
    }

    /**
     * Remove input consumer.
     * 
     * @param c to remove
     */
    public void removeTextInputConsumer(TextInputConsumer c) {
        this.consumer.remove(c);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

}
