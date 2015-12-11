/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.client.common.data;

import java.util.Map;

/**
 * Element for UI in game.
 * 
 * @author Josch Bosch
 */
public abstract class UIElement {

    /**
     * How the elements are arranged. The lesser the zOrder, the more in the back the element will be rendered.
     */
    public int zOrder = 0;

    /**
     * The ID for the view, corresponding to the UI element.
     */
    public String uiViewId = null;

    private boolean visible = true;

    public UIElement(String id, int order) {
        this.uiViewId = id;
        this.zOrder = order;
    }

    public String getUiViewId() {
        return uiViewId;
    }

    public void setUiViewId(String uiViewId) {
        this.uiViewId = uiViewId;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Handle input.
     * 
     * @param keyMap to handle
     */
    public void handleInput(Map<Integer, Integer> keyMap) {

    }
}
