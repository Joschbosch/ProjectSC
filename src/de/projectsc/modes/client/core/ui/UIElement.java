/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.modes.client.core.ui;

/**
 * Element for UI in game.
 * 
 * @author Josch Bosch
 */
public abstract class UIElement {

    /**
     * The ID for the view, corresponding to the UI element.
     */
    public String id = null;

    private boolean visible = true;

    public UIElement(String id, int order) {
        this.id = id;
        UIManager.registerElement(this);

    }

    public String getUiViewId() {
        return id;
    }

    public void setUiViewId(String uiViewId) {
        this.id = uiViewId;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}
