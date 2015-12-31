/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.data;

/**
 * Implementation of an input command for the keyboard.
 * 
 * @author Josch Bosch
 */
public class KeyboardInputCommand implements InputCommand {

    private final int key;

    private final char character;

    private final boolean keyDown;

    private final boolean shiftDown;

    private final boolean controlDown;

    private boolean consumed = false;

    private boolean keyRepeated;

    public KeyboardInputCommand(int key, char character, boolean keyDown, boolean shiftDown, boolean controlDown, boolean keyRepeated) {
        this.key = key;
        this.character = character;
        this.keyDown = keyDown;
        this.shiftDown = shiftDown;
        this.controlDown = controlDown;
        this.keyRepeated = keyRepeated;
    }

    @Override
    public void consume() {
        consumed = true;
    }

    @Override
    public boolean isConsumed() {
        return consumed;
    }

    public int getKey() {
        return key;
    }

    public char getCharacter() {
        return character;
    }

    public boolean isKeyDown() {
        return keyDown;
    }

    public boolean isKeyRepeated() {
        return keyRepeated;
    }

    public boolean isShiftDown() {
        return shiftDown;
    }

    public boolean isControlDown() {
        return controlDown;
    }

}
