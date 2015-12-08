/*
 * Copyright (C) 2015
 */

package de.projectsc.core.modes.client.gui.components;

import de.projectsc.core.entities.Component;
import de.projectsc.core.modes.client.gui.data.Scene;

/**
 * A component that has a graphical representation.
 * 
 * @author Josch Bosch
 */
public abstract class GraphicalComponent extends Component {

    /**
     * Adds everything for rendering.
     * 
     * @param entity to render
     * @param scene for positions
     */
    public abstract void render(long entity, Scene scene);

}
