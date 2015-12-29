/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component.impl.physic;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.data.Scene;
import de.projectsc.core.data.physics.AxisAlignedBoundingBox;
import de.projectsc.core.data.physics.Transform;
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
        setID(NAME);
    }

    @Override
    public void update(long elapsed) {
        if (this.axisAlignedBoundingBox == null) {
            this.axisAlignedBoundingBox = new AxisAlignedBoundingBox(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
        }
    }

    @Override
    public boolean isValidForSaving() {
        return true;
    }

    @Override
    public Map<String, Object> serialize(File savingLocation) {
        return new HashMap<>();
    }

    @Override
    public void deserialize(Map<String, Object> serialized, File loadingLocation) {

    }

    /**
     * Check if the current entity intersects with the picking ray.
     * 
     * @param org position of camera
     * @param ray to intersect
     * @return true if it intersects
     */
    public float intersects(Transform transform, Vector3f org, Vector3f ray) {
        Vector3f lb = Vector3f.add(transform.getPosition(), axisAlignedBoundingBox.getMin(), null);
        Vector3f rt = Vector3f.add(transform.getPosition(), axisAlignedBoundingBox.getMax(), null);
        // r.dir is unit direction vector of ray
        float dirfracx = 1.0f / ray.x;
        float dirfracy = 1.0f / ray.y;
        float dirfracz = 1.0f / ray.z;
        // lb is the corner of AABB with minimal coordinates - left bottom, rt is maximal corner
        // r.org is origin of ray
        float t1 = (lb.x - org.x) * dirfracx;
        float t2 = (rt.x - org.x) * dirfracx;
        float t3 = (lb.y - org.y) * dirfracy;
        float t4 = (rt.y - org.y) * dirfracy;
        float t5 = (lb.z - org.z) * dirfracz;
        float t6 = (rt.z - org.z) * dirfracz;

        float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        float t;
        // if tmax < 0, ray (line) is intersecting AABB, but whole AABB is behing us
        if (tmax < 0) {
            t = tmax;
            return -1;
        }

        // if tmin > tmax, ray doesn't intersect AABB
        if (tmin > tmax) {
            t = tmax;
            return -1;
        }

        t = tmin;
        return t;
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
