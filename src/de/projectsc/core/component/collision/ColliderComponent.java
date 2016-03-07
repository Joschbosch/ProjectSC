/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component.collision;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.data.physics.AxisAlignedBoundingBox;
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

    private AxisAlignedBoundingBox axisAlignedBoundingBox;

    public ColliderComponent() {
        setType(ComponentType.PREPHYSICS);
        setComponentName(NAME);
        if (this.axisAlignedBoundingBox == null) {
            this.axisAlignedBoundingBox = new AxisAlignedBoundingBox(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
        }
    }

    @Override
    public boolean isValidForEntitySaving() {
        return true;
    }

    @Override
    public Component cloneComponent() {
        System.out.println("TO DO");
        return new ColliderComponent();
    }

    public AxisAlignedBoundingBox getAABB() {
        return axisAlignedBoundingBox;
    }

    public AxisAlignedBoundingBox getAxisAlignedBoundingBox() {
        return axisAlignedBoundingBox;
    }
}
