/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.core.modes.client.common.ui.elements;

/**
 * Element for UI in game.
 * 
 * @author Josch Bosch
 */
public abstract class UIElement {

    /**
     * How the elements are arranged. The lesser the zOrder, the more in the back the element will
     * be rendered.
     */
    public int zOrder = 0;
}
