/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.data;

public class MouseInputCommand implements InputCommand {

    private final int mouseX;

    private final int mouseY;

    private final int mouseWheel;

    private final int button;

    private final boolean buttonDown;

    private boolean consumed = false;

    private int mouseDX;

    private int mouseDY;

    private boolean repeatedDown;

    public MouseInputCommand(int mouseX, int mouseY, int mouseDX, int mouseDY, int mouseWheel, int button, boolean buttonDown,
        boolean repeatedDown) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.mouseWheel = mouseWheel;
        this.button = button;
        this.buttonDown = buttonDown;
        this.mouseDX = mouseDX;
        this.mouseDY = mouseDY;
        this.repeatedDown = repeatedDown;
    }

    @Override
    public void consumed() {
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

    public boolean isButtonDown() {
        return buttonDown;
    }

    public boolean isRepeatedDown() {
        return repeatedDown;
    }
}
