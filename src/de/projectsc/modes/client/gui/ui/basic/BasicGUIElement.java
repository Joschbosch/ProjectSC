/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.ui.basic;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.core.data.Event;
import de.projectsc.core.manager.EventManager;
import de.projectsc.modes.client.core.data.MouseInputCommand;
import de.projectsc.modes.client.core.manager.ClientEventManager;
import de.projectsc.modes.client.gui.input.InputConsumeManager;
import de.projectsc.modes.client.gui.ui.GUIElement;

public abstract class BasicGUIElement implements GUIElement {

    protected InputConsumeManager inputConsumeManager;

    protected Vector4f positionAndSize;

    private EventManager eventManager;

    private boolean visible = true;

    private boolean active = true;

    public BasicGUIElement() {
        this.eventManager = ClientEventManager.getInstance();
        this.inputConsumeManager = InputConsumeManager.getInstance();
    }

    protected void fireEvent(Event e) {
        eventManager.fireEvent(e);
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean value) {
        this.visible = value;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public boolean hit(MouseInputCommand command) {
        boolean hit = command.getMouseX() >= convertWidthToPixel(positionAndSize.x)
            && command.getMouseX() <= convertWidthToPixel(positionAndSize.x + positionAndSize.z)
            && command.getMouseY() >= convertHeightToPixel(positionAndSize.y)
            && command.getMouseY() <= convertHeightToPixel(positionAndSize.y + positionAndSize.w);
        return hit && isActive();
    }

    private int convertHeightToPixel(float y) {
        return (int) (Display.getHeight() * y);
    }

    private int convertWidthToPixel(float x) {
        return (int) (Display.getWidth() * x);
    }

    public Vector4f getPositionAndSize() {
        return positionAndSize;
    }
}
