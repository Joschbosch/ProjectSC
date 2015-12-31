/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.ui.basic;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.modes.client.core.data.KeyboardInputCommand;
import de.projectsc.modes.client.core.data.MouseInputCommand;
import de.projectsc.modes.client.core.interfaces.InputCommandListener;
import de.projectsc.modes.client.gui.data.UI;
import de.projectsc.modes.client.gui.textures.UITexture;
import de.projectsc.modes.client.gui.ui.spi.GUIElement;
import de.projectsc.modes.client.gui.utils.Loader;

/**
 * Container for other elements in the GUI.
 * 
 * @author Josch Bosch
 */
public class Container extends ContainerElement implements InputCommandListener {

    protected List<GUIElement> elements;

    private boolean visible = true;

    public Container() {
        super(null, new Vector2f(0, 0));
        elements = new LinkedList<>();
        positionAndSize = new Vector4f(0.0f, 0.0f, 1f, 1f);
        inputConsumeManager.addListener(this);
    }

    public Container(Container c) {
        super(null, new Vector2f(0, 0));

        elements = new LinkedList<>();
        positionAndSize = new Vector4f(0, 0, 1, 1);
        c.add(this);
        inputConsumeManager.addListener(this);
    }

    public Container(Container c, Vector4f positionAndSize) {
        super(c, new Vector2f(positionAndSize.x, positionAndSize.y));
        elements = new LinkedList<>();
        this.positionAndSize = positionAndSize;
        c.add(this);
        inputConsumeManager.addListener(this);
    }

    @Override
    public void setZOrder(int zOrder) {
        super.setZOrder(zOrder);
        for (GUIElement element : elements) {
            ((ContainerElement) element).setZOrder(zOrder);
        }
    }

    @Override
    public void render(UI ui) {
        if (isVisible()) {
            if (bg != null) {
                ui.addElement(bg, zOrder);
            }
        }
        for (GUIElement element : elements) {
            element.render(ui);
        }
    }

    public void setPositionAndSize(Vector4f positionAndSize) {
        this.positionAndSize = positionAndSize;
    }

    /**
     * Set new Position of container. Update relative positions of sub elements.
     * 
     * @param position to set
     */
    public void setPosition(Vector2f position) {
        this.positionAndSize.x = position.x;
        this.positionAndSize.y = position.y;
        for (GUIElement element : elements) {
            ((ContainerElement) element).setRelativePosition(position);
        }
        update();
    }

    protected void update() {
        Vector2f position = getPosition();
        if (container != null) {
            position = new Vector2f(-1 + positionAndSize.z + 2 * positionAndSize.x,
                1 - positionAndSize.w - 2 * positionAndSize.y);
        }
        bg =
            new UITexture(Loader.loadTexture(backgroundFile), position,
                new Vector2f(
                    positionAndSize.z, positionAndSize.w));
    }

    /**
     * Set size of container.
     * 
     * @param size to set
     */
    public void setSize(Vector2f size) {
        this.positionAndSize.z = size.x;
        this.positionAndSize.w = size.y;
        update();
    }

    /**
     * Adds a background to the container.
     * 
     * @param backgroundFile to load
     */
    public void setBackground(String backgroundFile) {
        this.backgroundFile = backgroundFile;
        update();
    }

    /**
     * Add new element to container.
     * 
     * @param element to add.
     */
    public void add(GUIElement element) {
        elements.add(element);
    }

    @Override
    public void setVisible(boolean value) {
        this.visible = value;
        for (GUIElement element : elements) {
            element.setVisible(value);
        }
    }

    @Override
    public void setActive(boolean value) {
        this.visible = value;
        for (GUIElement element : elements) {
            element.setActive(value);
        }
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public InputConsumeLevel getInputConsumeLevel() {
        if (zOrder == 0) {
            return InputConsumeLevel.SECOND;
        } else {
            return InputConsumeLevel.FIRST;
        }
    }

    @Override
    public void handleKeyboardCommand(KeyboardInputCommand command) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleMouseCommand(MouseInputCommand command) {}

}
