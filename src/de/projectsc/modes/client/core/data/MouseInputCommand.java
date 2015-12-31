/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.core.data;

/**
 * Implementation of input command for the mouse.
 * 
 * @author Josch Bosch
 */
public class MouseInputCommand implements InputCommand {

    private final int mouseX;

    private final int mouseY;

    private final int mouseWheel;

    private final int button;

    private final boolean[] buttonDown;

    private boolean consumed = false;

    private int mouseDX;

    private int mouseDY;

    private boolean repeatedDown;

    public MouseInputCommand(int mouseX, int mouseY, int mouseDX, int mouseDY, int mouseWheel, int button, boolean[] buttonsDown,
        boolean repeatedDown) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.mouseWheel = mouseWheel;
        this.button = button;
        this.buttonDown = buttonsDown;
        this.mouseDX = mouseDX;
        this.mouseDY = mouseDY;
        this.repeatedDown = repeatedDown;
    }

    @Override
    public void consume() {
        consumed = true;
    }

    @Override
    public boolean isConsumed() {
        return consumed;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public int getMouseDX() {
        return mouseDX;
    }

    public int getMouseDY() {
        return mouseDY;
    }

    public int getMouseWheel() {
        return mouseWheel;
    }

    public int getButton() {
        return button;
    }

    /**
     * Check if the buttin with given id is pressed.
     * 
     * @param buttonID to get
     * @return true, if it is down
     */
    public boolean isButtonDown(int buttonID) {
        return buttonDown[buttonID];
    }

    public boolean isRepeatedDown() {
        return repeatedDown;
    }
}
