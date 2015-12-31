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
import de.projectsc.modes.client.gui.textures.UITexture;
import de.projectsc.modes.client.gui.ui.spi.GUIElement;

/**
 * 
 * Basic class for GUI elements in the GUI.
 * 
 * @author Josch Bosch
 */
public abstract class BasicGUIElement implements GUIElement, Comparable<BasicGUIElement> {

    protected InputConsumeManager inputConsumeManager;

    protected Vector4f positionAndSize;

    protected int zOrder = 0;

    protected UITexture bg;

    protected String backgroundFile;

    protected boolean visible = true;

    protected boolean active = true;

    private EventManager eventManager;

    private boolean highlighted = false;

    private boolean consumesClick = false;

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

    /**
     * Check if this element is hit by the mouse.
     * 
     * @param command to check
     * @return true, if it is active and hit
     */
    public boolean hit(MouseInputCommand command) {
        boolean hit = command.getMouseX() >= convertWidthToPixel(positionAndSize.x)
            && command.getMouseX() <= convertWidthToPixel(positionAndSize.x + positionAndSize.z)
            && command.getMouseY() >= convertHeightToPixel(positionAndSize.y)
            && command.getMouseY() <= convertHeightToPixel(positionAndSize.y + positionAndSize.w);
        return hit && isActive();
    }

    /**
     * 
     * @return true, if the click should be consumed
     */
    public boolean consumesClick() {
        return consumesClick;
    }

    public void setConsumesClick(boolean consumesClick) {
        this.consumesClick = consumesClick;
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

    @Override
    public int compareTo(BasicGUIElement o) {
        return new Integer(this.zOrder).compareTo(new Integer(o.zOrder));
    }

    public int getZOrder() {
        return zOrder;
    }

    public void setZOrder(int incZOrder) {
        this.zOrder = incZOrder;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }
}
