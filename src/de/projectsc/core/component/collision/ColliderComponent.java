/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component.collision;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.data.physics.BoundingVolume;
import de.projectsc.core.interfaces.Component;

/**
 * When this component is attached to an entity, it will be able to collide with the world or being able to be selected.
 *
 * It has different modules that represent different collision boxes.
 *
 * @author Josch Bosch
 */
public class ColliderComponent extends DefaultComponent {

    /**
     * ID.
     */
    public static final String NAME = "Collider Component";

    private BoundingVolume simpleBoundingVolume;

    private BoundingVolume exactBoundingVolume;

    public ColliderComponent() {
        setType(ComponentType.PREPHYSICS);
        setComponentName(NAME);
    }

    @Override
    public boolean isValidForEntitySaving() {
        return true;
    }

    @Override
    public Component cloneComponent() {
        ColliderComponent cc = new ColliderComponent();
        cc.setSimpleBoundingVolume(getSimpleBoundingVolume().cloneVolume());
        return cc;
    }

    public BoundingVolume getSimpleBoundingVolume() {
        return simpleBoundingVolume;
    }

    public void setSimpleBoundingVolume(BoundingVolume boundingVolume) {
        this.simpleBoundingVolume = boundingVolume;
    }

    public BoundingVolume getExactBoundingVolume() {
        return exactBoundingVolume;
    }

    public void setExactBoundingVolume(BoundingVolume exactBoundingVolume) {
        this.exactBoundingVolume = exactBoundingVolume;
    }
}
