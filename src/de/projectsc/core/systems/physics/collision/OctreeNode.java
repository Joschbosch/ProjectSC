/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.systems.physics.collision;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.collision.ColliderComponent;
import de.projectsc.core.component.physic.TransformComponent;
import de.projectsc.core.data.physics.BoundingVolume;
import de.projectsc.core.data.physics.BoundingVolumeType;
import de.projectsc.core.manager.EntityManager;

public class OctreeNode {

    private static final Log LOGGER = LogFactory.getLog(OctreeNode.class);

    private static final int MAXIMUM_LIFESPAN = 64;

    private static final int LIFE_INIT_VALUE = -1;

    private static final int LEAF_CAPACITY = 1;

    private static final int MAX_DEPTH = 20;

    private final Vector3f center;

    private final float halfSize;

    private EntityManager entityManager;

    private OctreeNode parent;

    private List<String> children;

    private List<String> entities;

    private List<String> pendingEntites;

    private int maxLifespan = 8;

    private int curLife = LIFE_INIT_VALUE;

    public OctreeNode(long initialSize, EntityManager entityManager) {
        center = new Vector3f(initialSize / 2.0f, initialSize / 2.0f, initialSize / 2.0f);
        halfSize = initialSize / 2.0f;
        parent = null;
        children = new ArrayList<String>(8);
        entities = new LinkedList<String>();
        pendingEntites = new LinkedList<String>();
        this.entityManager = entityManager;
        if (initialSize == 0) {
            throw new RuntimeException("Size is zero");
        }
        if ((initialSize & (initialSize - 1)) != 0) {
            throw new RuntimeException("Size is no power of 2");
        }
    }

    public void insert(String entity) {
        if (entityManager.hasComponent(entity, TransformComponent.class)
            && entityManager.hasComponent(entity, ColliderComponent.class)) {
            pendingEntites.add(entity);
        }
    }

    public void remove(String entity) {

    }

    public OctreeNode update(List<String> movedEntities) {
        OctreeNode newRoot = this;
        for (String newEntity : entities) {
            OctreeNode returnValue = newRoot.insertNewEntity(newEntity);
            if (returnValue != null) {
                newRoot = returnValue;
            }
        }

        return newRoot;
    }

    public List<IntersectionRecord> getIntersections() {
        return null;
    }

    private OctreeNode insertNewEntity(String newEntity) {

        if (containsEntity(newEntity)) { // Entity will be added in current node or any child
            if (isLeaf() && entities.size() < LEAF_CAPACITY) {
                entities.add(newEntity);
            } else if (entities.size() >= LEAF_CAPACITY) {
                createChildren(newEntity);
            } else if (!isLeaf()) {
                boolean foundChild = searchForFittingChild(newEntity);
                if (!foundChild) {
                    entities.add(newEntity);
                }
            }
        } else if (parent == null) { // this is the root node and it does not contain entity -> expand

        }
        return null;
    }

    private void createChildren(String newEntity) {

    }

    private boolean searchForFittingChild(String newEntity) {
        return false;
    }

    private boolean containsEntity(String newEntity) {
        return false;
    }

    private boolean isLeaf() {
        return children.size() == 0;
    }

    public Vector3f getNodeWorldRenderingPosition() {
        return Vector3f.sub(center, new Vector3f(halfSize, halfSize, halfSize), null);
    }

    public Vector3f getNodeWorldScale() {
        return new Vector3f(2 * halfSize, 2 * halfSize, 2 * halfSize);
    }

    private Vector3f getEntityPosition(String entity) {
        if (entityManager.hasComponent(entity, TransformComponent.class)) {
            return ((TransformComponent) entityManager.getComponent(entity, TransformComponent.class)).getPosition();
        }
        return null;
    }

    private BoundingVolumeType getBoundingVolumeType(String entity) {
        if (entityManager.hasComponent(entity, ColliderComponent.class)) {
            return ((ColliderComponent) entityManager.getComponent(entity, ColliderComponent.class)).getSimpleBoundingVolume().getType();
        }
        return null;
    }

    private BoundingVolume getBoundingVolume(String entity) {
        if (entityManager.hasComponent(entity, ColliderComponent.class)) {
            return ((ColliderComponent) entityManager.getComponent(entity, ColliderComponent.class)).getSimpleBoundingVolume();
        }
        return null;
    }

    private Vector3f getBoundingsWorldPosition(String entity) {
        if (entityManager.hasComponent(entity, TransformComponent.class)
            && entityManager.hasComponent(entity, ColliderComponent.class)) {
            BoundingVolume bv = getBoundingVolume(entity);
            Vector3f entityPosition = getEntityPosition(entity);
            return Vector3f.add(bv.getPositionOffset(), entityPosition, null);
        }
        return null;
    }
}
