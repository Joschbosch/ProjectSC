/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import de.projectsc.modes.client.core.data.InputCommand;
import de.projectsc.modes.client.core.data.KeyboardInputCommand;
import de.projectsc.modes.client.core.data.MouseInputCommand;

/**
 * System for handling input.
 * 
 * @author Josch Bosch
 */
public class InputSystem {

    private static final Log LOGGER = LogFactory.getLog(InputSystem.class);

    private boolean shiftDown;

    private boolean controlDown;

    private boolean[] keysDown = new boolean[0xDB];

    private boolean[] buttonsDown = new boolean[5];

    public InputSystem() {
        try {
            Mouse.create();
            Keyboard.create();
            Keyboard.enableRepeatEvents(true);
        } catch (LWJGLException e) {
            LOGGER.error("Failed to init input system: ", e);
        }
    }

    /**
     * Update all inputs.
     * 
     * @return commands from inputs
     */
    public Queue<InputCommand> updateInputs() {
        Queue<InputCommand> currentInputs = new LinkedList<>();
        getMouseEvents(currentInputs);
        getKeyboardEvents(currentInputs);
        return currentInputs;
    }

    private void getMouseEvents(Queue<InputCommand> currentInputs) {
        while (Mouse.next()) {

            int mouseX = Mouse.getEventX();
            int mouseY = Mouse.getEventY();
            int mouseDX = Mouse.getEventDX();
            int mouseDY = Mouse.getEventDY();
            int mouseWheel = Mouse.getEventDWheel();
            int button = Mouse.getEventButton();
            boolean buttonDown = Mouse.getEventButtonState();
            boolean repeatedDown = false;
            if (button != -1) {
                if (buttonsDown[button] && buttonDown) {
                    repeatedDown = true;
                }
                buttonsDown[button] = buttonDown;
            }
            currentInputs.offer(new MouseInputCommand(mouseX, mouseY, mouseDX, mouseDY, mouseWheel, button, buttonsDown, repeatedDown));

        }
    }

    private void getKeyboardEvents(Queue<InputCommand> currentInputs) {
        while (Keyboard.next()) {
            currentInputs.offer(createEvent(Keyboard.getEventKey(), Keyboard.getEventCharacter(), Keyboard.getEventKeyState()));
        }
    }

    private KeyboardInputCommand createEvent(final int key, final char character, final boolean keyDown) {
        if (isShiftDown(key, keyDown)) {
            shiftDown = true;
        } else if (isShiftUp(key, keyDown)) {
            shiftDown = false;
        } else if (isControlDown(key, keyDown)) {
            controlDown = true;
        } else if (isControlUp(key, keyDown)) {
            controlDown = false;
        }
        boolean repeatedDown = false;
        if (keysDown[key] && keyDown) {
            repeatedDown = true;
        }
        keysDown[key] = keyDown;
        return new KeyboardInputCommand(key, character, keyDown, shiftDown, controlDown, repeatedDown);
    }

    private boolean isShiftKey(final int key) {
        return key == Keyboard.KEY_LSHIFT || key == Keyboard.KEY_RSHIFT;
    }

    private boolean isShiftUp(final int key, final boolean keyDown) {
        return !keyDown && isShiftKey(key);
    }

    private boolean isShiftDown(final int key, final boolean keyDown) {
        return keyDown && isShiftKey(key);
    }

    private boolean isControlKey(final int key) {
        return key == Keyboard.KEY_RCONTROL || key == Keyboard.KEY_LCONTROL || key == Keyboard.KEY_LMETA || key == Keyboard.KEY_RMETA;
    }

    private boolean isControlDown(final int key, final boolean keyDown) {
        return keyDown && isControlKey(key);
    }

    private boolean isControlUp(final int key, final boolean keyDown) {
        return !keyDown && isControlKey(key);
    }

    /**
     * Dispose system.
     */
    public void dispose() {
        Mouse.destroy();
        Keyboard.destroy();
    }

}
