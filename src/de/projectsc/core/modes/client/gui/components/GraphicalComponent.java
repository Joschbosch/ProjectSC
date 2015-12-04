/*
 * Copyright (C) 2015
 */

package de.projectsc.core.modes.client.gui.components;

import de.projectsc.core.entities.Component;
import de.projectsc.core.entities.Entity;
import de.projectsc.core.modes.client.gui.Scene;

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
