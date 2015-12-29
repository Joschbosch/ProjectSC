/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui.components;

import de.projectsc.core.component.DefaultComponent;
import de.projectsc.modes.client.gui.data.GUIScene;

/**
 * A component that has a graphical representation.
 * 
 * @author Josch Bosch
 */
public abstract class GraphicalComponent extends DefaultComponent {

    /**
     * Adds everything for rendering.
     * 
     * @param entity to render
     * @param scene for positions
     */
    public abstract void render(String entity, GUIScene scene);

    @Override
    public void update(long elapsed) {
        // TODO Auto-generated method stub

    }
}
