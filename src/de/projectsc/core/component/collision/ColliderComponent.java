/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component.collision;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.physic.PhysicsComponent;
import de.projectsc.core.data.Scene;
import de.projectsc.core.data.physics.AxisAlignedBoundingBox;
import de.projectsc.core.data.physics.WireFrame;

/**
 * When this component is attached to an entity, it will be able to collide with the world or being able to be selected.
 *
 * It has different modules that represent different collision boxes.
 *
 * @author Josch Bosch
 */
public class ColliderComponent extends PhysicsComponent {

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
    public boolean isValidForSaving() {
        return true;
    }

    @Override
    public void addSceneInformation(Scene scene) {
        WireFrame wf =
            new WireFrame(WireFrame.CUBE, owner.getTransform().getPosition(), new Vector3f(),
                axisAlignedBoundingBox.getSize());
        wf.setColor(new Vector3f(1.0f, 0, 0));
        scene.getWireFrames().add(wf);
    }

    public AxisAlignedBoundingBox getAABB() {
        return axisAlignedBoundingBox;
    }

}
