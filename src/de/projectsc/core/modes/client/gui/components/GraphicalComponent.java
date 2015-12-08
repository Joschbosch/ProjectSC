/*
 * Copyright (C) 2015
 */

package de.projectsc.core.modes.client.gui.components;

import de.projectsc.core.data.entities.Component;
import de.projectsc.core.data.entities.Entity;
import de.projectsc.core.modes.client.gui.data.Scene;

/**
 * A component that has a graphical representation.
 * 
 * @author Josch Bosch
 */
public abstract class GraphicalComponent extends Component {

    public GraphicalComponent(String newName, Entity owner) {
        super(newName, owner);
    }

    /**
     * Adds everything for rendering.
     * 
     * @param scene for positions
     */
    public abstract void render(Scene scene);
}
