/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.ui.basic;

import org.lwjgl.util.vector.Vector2f;

/**
 * Elements of a container, they have only a relative position.
 * 
 * @author Josch Bosch
 */
public abstract class ContainerElement extends BasicGUIElement {

    protected final Container container;

    protected Vector2f positionInContainer;

    public ContainerElement(Container c, Vector2f position) {
        super();
        this.container = c;
        setRelativePosition(position);
    }

    /**
     * Set new position of the element relative to its container position.
     * 
     * @param position to set.
     */
    public void setRelativePosition(Vector2f position) {
        if (container != null) {
            this.positionInContainer =
                new Vector2f(container.getPositionAndSize().x + position.x * container.getPositionAndSize().z,
                    container.getPositionAndSize().y + position.y * container.getPositionAndSize().w);
        } else {
            positionInContainer = position;
        }
    }

    /**
     * Gets the sscreen position of the element.
     * 
     * @return absoluite position on sceen.
     */
    public Vector2f getPosition() {
        if (container != null) {
            return positionInContainer;
        } else {
            return new Vector2f(-1 + positionAndSize.z + 2 * positionAndSize.x,
                1 - positionAndSize.w - 2 * positionAndSize.y);
        }
    }
}
