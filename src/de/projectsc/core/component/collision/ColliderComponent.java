/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component.collision;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.data.physics.BoundingVolume;
import de.projectsc.core.data.physics.BoundingVolumeType;
import de.projectsc.core.data.physics.boundings.AxisAlignedBoundingBox;
import de.projectsc.core.data.physics.boundings.Sphere;
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
        simpleBoundingVolume = new AxisAlignedBoundingBox(new Vector3f(), new Vector3f(1, 1, 1));
        exactBoundingVolume = new Sphere();

    }

    @Override
    public Map<String, Object> serialize(File savingLocation) {
        Map<String, Object> result = new HashMap<>();
        result.put("simple", simpleBoundingVolume);
        result.put("exact", exactBoundingVolume);
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deserialize(Map<String, Object> serialized, String loadingLocation) {
        System.out.println(serialized);
        simpleBoundingVolume = deserialize((Map<String, Object>) serialized.get("simple"));
        exactBoundingVolume = deserialize((Map<String, Object>) serialized.get("exact"));

    }

    @SuppressWarnings("unchecked")
    private BoundingVolume deserialize(Map<String, Object> serialized) {
        BoundingVolume result = null;
        Vector3f scale = getVectorDeserialized((Map<String, Double>) serialized.get("scale"));
        Vector3f position = getVectorDeserialized((Map<String, Double>) serialized.get("positionOffset"));

        if (BoundingVolumeType.valueOf((String) serialized.get("type")) == BoundingVolumeType.AXIS_ALIGNED_BOUNDING_BOX) {
            Vector3f min = getVectorDeserialized((Map<String, Double>) serialized.get("minima"));
            Vector3f max = getVectorDeserialized((Map<String, Double>) serialized.get("maxima"));
            result = new AxisAlignedBoundingBox(min, max);
        } else if (BoundingVolumeType.valueOf((String) serialized.get("type")) == BoundingVolumeType.SPHERE) {
            result = new Sphere();
            ((Sphere) result).setRadius((float) (double) serialized.get("radius"));
        }
        result.setPositionOffset(position);
        result.setScale(scale);
        return result;
    }

    private Vector3f getVectorDeserialized(Map<String, Double> serialized) {
        return new Vector3f((float) (double) serialized.get("x"), (float) (double) serialized.get("y"),
            (float) (double) serialized.get("z"));
    }

    @Override
    public Component cloneComponent() {
        ColliderComponent cc = new ColliderComponent();
        cc.setSimpleBoundingVolume(getSimpleBoundingVolume().cloneVolume());
        cc.setExactBoundingVolume(getExactBoundingVolume().cloneVolume());
        return cc;
    }

    public BoundingVolume getSimpleBoundingVolume() {
        return simpleBoundingVolume;
    }

    public void setSimpleBoundingVolume(BoundingVolume boundingVolume) {
        this.simpleBoundingVolume = boundingVolume;
    }

    /**
     * @return exact bounding volume or, if it is null, the simple volume.
     */
    public BoundingVolume getExactBoundingVolume() {
        if (exactBoundingVolume == null) {
            return simpleBoundingVolume;
        }
        return exactBoundingVolume;
    }

    public void setExactBoundingVolume(BoundingVolume exactBoundingVolume) {
        this.exactBoundingVolume = exactBoundingVolume;
    }
}
