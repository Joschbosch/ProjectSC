/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.physics.boundings;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.physics.BoundingVolume;
import de.projectsc.core.data.physics.BoundingVolumeType;

public class MeshBounding implements BoundingVolume {

    private Vector3f position;

    private Vector3f scale;

    private int meshId;

    public MeshBounding() {
        position = new Vector3f();
        scale = new Vector3f(scale);
    }

    @Override
    public Vector3f getPositionOffset() {
        return position;
    }

    @Override
    public Vector3f getScale() {
        return scale;
    }

    @Override
    public Vector3f getMinima() {
        return new Vector3f();
    }

    @Override
    public Vector3f getMaxima() {
        return new Vector3f();
    }

    @Override
    public BoundingVolumeType getType() {
        return BoundingVolumeType.MESH;
    }

    @Override
    public BoundingVolume cloneVolume() {
        MeshBounding mesh = new MeshBounding();
        mesh.setMeshId(meshId);
        return mesh;
    }

    public void setMeshId(int meshId) {
        this.meshId = meshId;
    }

    public int getMeshId() {
        return meshId;
    }
}
