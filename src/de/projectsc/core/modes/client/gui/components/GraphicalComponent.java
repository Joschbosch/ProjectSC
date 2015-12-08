/*
 * Copyright (C) 2015
 */

package de.projectsc.core.modes.client.gui.components;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.entities.Component;
import de.projectsc.core.modes.client.gui.data.Scene;

/**
 * A component that has a graphical representation.
 * 
 * @author Josch Bosch
 */
public abstract class GraphicalComponent extends Component {

    private Vector3f entityPosition;

    private Vector3f entityRotation;

    public void setNewPosition(Vector3f entityPosition, Vector3f entityRotation) {
        this.setEntityPosition(entityPosition);
        this.setEntityRotation(entityRotation);
    }

    /**
     * Adds everything for rendering.
     * 
     * @param scene for positions
     */
    public abstract void render(Scene scene);

    public Vector3f getEntityPosition() {
        return entityPosition;
    }

    public void setEntityPosition(Vector3f entityPosition) {
        this.entityPosition = entityPosition;
    }

    public Vector3f getEntityRotation() {
        return entityRotation;
    }

    public void setEntityRotation(Vector3f entityRotation) {
        this.entityRotation = entityRotation;
    }
}
