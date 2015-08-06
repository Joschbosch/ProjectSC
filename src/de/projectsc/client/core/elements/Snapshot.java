/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.core.elements;

import java.util.LinkedList;
import java.util.List;

/**
 * Snapshot to render.
 * 
 * @author Josch Bosch
 */
public class Snapshot {

    private final List<UIElement> ui;

    public Snapshot() {
        ui = new LinkedList<>();
    }

    /**
     * 
     * @param element to render.
     */
    public void addUIElement(UIElement element) {
        ui.add(element);
    }

    public List<UIElement> getUIElements() {
        return ui;
    }
}
