/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.ui.basic;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import de.projectsc.modes.client.gui.data.UI;
import de.projectsc.modes.client.gui.textures.UITexture;
import de.projectsc.modes.client.gui.ui.GUIElement;
import de.projectsc.modes.client.gui.utils.Loader;

public class Container implements GUIElement {

    private List<GUIElement> elements;

    private UITexture bg;

    private Vector4f positionAndSize;

    private String backgroundFile;

    private boolean visible = true;

    private int uiOrder = UI.BACKGROUND;

    public Container() {
        elements = new LinkedList<>();
        positionAndSize = new Vector4f(0, 0, 1, 1);
    }

    public Container(Container c) {
        elements = new LinkedList<>();
        positionAndSize = new Vector4f(0, 0, 1, 1);
        c.add(this);
    }

    public Container(Container c, Vector4f positionAndSize) {
        elements = new LinkedList<>();
        this.positionAndSize = positionAndSize;
        c.add(this);
    }

    public void setOrder(int order) {
        uiOrder = order;
        for (GUIElement element : elements) {
            if (element instanceof Label) {
                ((Label) element).setRenderOrder(order);
            }
        }
    }

    @Override
    public void render(UI ui) {
        if (isVisible()) {
            if (bg != null) {
                ui.addElement(bg, uiOrder);
            }
            for (GUIElement element : elements) {
                element.render(ui);
            }
        }
    }

    public void setPositionAndSize(Vector4f positionAndSize) {
        this.positionAndSize = positionAndSize;
    }

    public void setPosition(Vector2f position) {
        this.positionAndSize.x = position.x;
        this.positionAndSize.y = position.y;
        update();
    }

    private void update() {
        bg =
            new UITexture(Loader.loadTexture(backgroundFile), new Vector2f(positionAndSize.x, positionAndSize.y), new Vector2f(
                positionAndSize.z, positionAndSize.w));
    }

    public void setSize(Vector2f position) {
        this.positionAndSize.z = position.x;
        this.positionAndSize.w = position.y;
        update();
    }

    public void setBackground(String backgroundFile) {
        this.backgroundFile = backgroundFile;
        update();
    }

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
    public boolean isVisible() {
        return visible;
    }
}
