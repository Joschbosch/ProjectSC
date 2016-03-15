/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.systems.physics.collision;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.Debug;
import de.projectsc.core.component.collision.ColliderComponent;
import de.projectsc.core.component.physic.TransformComponent;
import de.projectsc.core.data.physics.BoundingVolume;
import de.projectsc.core.data.physics.BoundingVolumeType;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.data.physics.boundings.AxisAlignedBoundingBox;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.utils.Maths;

/**
 * One node in the octree for the collision system.
 * 
 * @author Josch Bosch
 */
public class OctreeNode {

    private static final Log LOGGER = LogFactory.getLog(OctreeNode.class);

    private static final int MAXIMUM_LIFESPAN = 64;

    private static final int LIFE_INIT_VALUE = -1;

    private static final int LEAF_CAPACITY = 1;

    private final float halfSize;

    private Vector3f center;

    private EntityManager entityManager;

    private OctreeNode parent;

    private OctreeNode[] children;

    private List<String> entities;

    private List<String> pendingEntites;

    private int maxLifespan = 8;

    private int curLife = LIFE_INIT_VALUE;

    private boolean isDirty;

    public OctreeNode(long initialSize, EntityManager entityManager) {
        center = new Vector3f(initialSize / 2.0f, initialSize / 2.0f, initialSize / 2.0f);
        halfSize = initialSize / 2.0f;
        parent = null;
        children = new OctreeNode[8];
        for (int i = 0; i < 8; i++) {
            children[i] = null;
        }
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

    public OctreeNode(float halfSize, OctreeNode parent, EntityManager manager) {
        this.parent = parent;
        this.halfSize = halfSize;
        this.entityManager = manager;
        pendingEntites = new LinkedList<String>();
        children = new OctreeNode[8];
        for (int i = 0; i < 8; i++) {
            children[i] = null;
        }
        entities = new LinkedList<String>();
    }

    public void insert(String entity) {
        if (entityManager.hasComponent(entity, TransformComponent.class)
            && entityManager.hasComponent(entity, ColliderComponent.class)) {
            pendingEntites.add(entity);
            isDirty = true;
        } else {
            LOGGER.error(String.format("Entity %s not added to collision system: Component missing", entity));
        }
    }

    public boolean remove(String entity) {
        if (!entities.contains(entity)) {
            boolean removed = false;
            for (int i = 0; i < 8 && !removed; i++) {
                if (children[i] != null && children[i].remove(entity)) {
                    Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "Successfully removed entity : " + entity);
                    return true;
                }
            }
        } else {
            entities.remove(entity);
            Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "Successfully removed entity : " + entity);
            return true;
        }
        Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "Could not remove entity : " + entity);
        return false;
    }

    public List<IntersectionRecord> getIntersections() {
        return new LinkedList<>();
    }

    public OctreeNode recalculateTree() {
        if (isDirty) {
            Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "Recalc tree");
            OctreeNode newRoot = this;
            for (String newEntity : pendingEntites) {
                newRoot.insertNewEntity(newEntity);
                while (newRoot.getParent() != null) {
                    newRoot = newRoot.getParent();
                }
            }
            Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "new tree: ");
            Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "entity Count: " + newRoot.traverseTree(new HashMap<>(), 0, true));
            isDirty = false;
            pendingEntites.clear();
            return newRoot;

        }
        return this;
    }

    public OctreeNode update(List<String> movedEntities) {
        OctreeNode newRoot = this;
        // count down and remove dead leafs

        if (entities.size() == 0) {
            if (isLeaf()) {
                if (curLife == LIFE_INIT_VALUE) {
                    curLife = maxLifespan;
                } else if (curLife > 0) {
                    curLife--;
                }
            }
        } else if (curLife != LIFE_INIT_VALUE) {
            if (maxLifespan <= MAXIMUM_LIFESPAN) {
                maxLifespan *= 2;
            }
            curLife = LIFE_INIT_VALUE;
        }
        List<String> movedInThisNode = new LinkedList<>();
        for (String entity : entities) {
            if (movedEntities.contains(entity)) {
                movedInThisNode.add(entity);
            }
        }
        movedEntities.removeAll(movedInThisNode);
        if (!isLeaf()) {
            for (int i = 0; i < 8; i++) {
                if (children[i] != null) {
                    children[i].update(movedEntities);
                }
            }
        }

        for (String movedEntity : movedInThisNode) {
            OctreeNode newParent = this;
            while (!newParent.containsEntity(movedEntity)) {
                if (newParent.getParent() != null) {
                    newParent = newParent.getParent();
                } else {
                    break;
                }
            }
            entities.remove(movedEntity);
            newParent.insertNewEntity(movedEntity);
        }

        for (int i = 0; i < 8; i++) {
            if (children[i] != null && children[i].curLife == 0) {
                children[i] = null;
                Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "KILL CHILD BECAUSE LIFE IS 0");
            }
        }
        return newRoot;
    }

    private void insertNewEntity(String newEntity) {
        Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "Insert entity " + newEntity);
        if (containsEntity(newEntity)) { // Entity will be added in current node or any child
            Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "contains");
            if (!isLeaf()) {
                Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "no leaf, search for child");
                boolean foundChild = searchForFittingChild(newEntity);
                if (!foundChild) {
                    Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "No child found");
                    entities.add(newEntity);
                }
            } else {
                entities.add(newEntity);
                Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "LEAF! ADDED NEW ENTTIY, SIZE = " + entities.size());
                if (entities.size() > LEAF_CAPACITY && halfSize > 0.5f) {
                    createChildren(newEntity);
                    Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "Was too big, created children : " + countChildren());
                }
            }
        } else if (parent == null) { // this is the root node and it does not contain entity -> expand
            OctreeNode newRoot = expandRoot(newEntity);
            newRoot.insertNewEntity(newEntity);
        }
    }

    private OctreeNode expandRoot(String newEntity) {
        OctreeNode newParent = new OctreeNode(halfSize * 2, null, entityManager);
        this.parent = newParent;
        // calc new center
        Transform entityPos = ((TransformComponent) entityManager.getComponent(newEntity, TransformComponent.class)).getTransform();

        float distance = Float.MAX_VALUE;
        Vector3f newCenter = null;
        for (int i = 0; i < 8; i++) {
            OctreeNode possibleParent = createChild(i, halfSize);
            float length = Vector3f.sub(entityPos.getPosition(), possibleParent.center, null).length();
            Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "COMPARE: " + length + " TO " + distance);
            if (length < distance) {
                distance = length;
                newCenter = possibleParent.center;
            }
        }
        if (newCenter != null) {
            newParent.setCenter(newCenter);
            newParent.createChildren(newEntity);
            Debug.printDebug(LOGGER, Debug.DEBUG_ALL,
                String.format("CREATED NEW PARENT WITH CENTER %s AND SIZE %s", newParent.getCenter(), newParent.halfSize));
            for (int i = 0; i < 8; i++) {
                Debug.printDebug(LOGGER, Debug.DEBUG_ALL, newParent.children[i].getCenter() + "    vs: " + this.getCenter());
                if (newParent.children[i] != null && newParent.children[i].getCenter().equals(this.getCenter())) {
                    newParent.children[i] = this;
                    Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "ADDED OLD TO NEW");
                }
            }
            return newParent;
        } else {
            LOGGER.error("Could not find new center position");
            return null;
        }

    }

    private Vector3f getCenter() {
        return center;
    }

    private void createChildren(String newEntity) {
        for (int i = 0; i < 8; i++) {
            children[i] = createChild(i, halfSize / 2.0f);
        }
        List<String> remove = new LinkedList<>();
        for (String entity : entities) {
            for (int i = 0; i < 8; i++) {
                Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "Check if entity " + entity + " fits into octant " + i);
                if (children[i].containsEntity(entity)) {
                    children[i].insertNewEntity(entity);
                    Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "put entity down to " + i);
                    remove.add(entity);
                    break;
                }
                Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "no!");
            }
        }
        entities.removeAll(remove);
    }

    private OctreeNode createChild(int i, float newSize) {
        OctreeNode child = new OctreeNode(newSize, this, entityManager);
        Vector3f newCenter = new Vector3f(center);
        // could be nicer: bit of int = 1 > -
        switch (i) {
        case 0:
            newCenter.x -= newSize;
            newCenter.y -= newSize;
            newCenter.z -= newSize;
            break;
        case 1:
            newCenter.x -= newSize;
            newCenter.y -= newSize;
            newCenter.z += newSize;
            break;
        case 2:
            newCenter.x -= newSize;
            newCenter.y += newSize;
            newCenter.z -= newSize;
            break;
        case 3:
            newCenter.x -= newSize;
            newCenter.y += newSize;
            newCenter.z += newSize;
            break;
        case 4:
            newCenter.x += newSize;
            newCenter.y -= newSize;
            newCenter.z -= newSize;
            break;
        case 5:
            newCenter.x += newSize;
            newCenter.y -= newSize;
            newCenter.z += newSize;
            break;
        case 6:
            newCenter.x += newSize;
            newCenter.y += newSize;
            newCenter.z -= newSize;
            break;
        case 7:
            newCenter.x += newSize;
            newCenter.y += newSize;
            newCenter.z += newSize;
            break;
        default:
            LOGGER.error("Wanted to create octreenode that not exists: " + i);
            break;
        }
        child.setCenter(newCenter);
        Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "Created new child: " + newCenter);
        return child;
    }

    private boolean searchForFittingChild(String newEntity) {
        for (int i = 0; i < 8; i++) {
            if (children[i] != null) {
                if (children[i].containsEntity(newEntity)) {
                    Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "found fitting child, insert");
                    children[i].insertNewEntity(newEntity);
                    return true;
                }
            } else {
                OctreeNode newChild = createChild(i, halfSize / 2.0f);
                Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "Must create new child for octant: " + newChild.getCenter());
                if (newChild.containsEntity(newEntity)) {
                    children[i] = newChild;
                    newChild.insertNewEntity(newEntity);
                    Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "created new child for entity.");
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean containsEntity(String newEntity) {
        if (getBoundingVolumeType(newEntity) == BoundingVolumeType.AXIS_ALIGNED_BOUNDING_BOX) {
            Vector3f entityPos = getEntityPosition(newEntity);
            Vector3f entityScale = getEntityScale(newEntity);

            Vector3f nodeMinimum = Vector3f.add(new Vector3f(-halfSize, -halfSize, -halfSize), center, null);
            Vector3f nodeMaxium = Vector3f.add(new Vector3f(halfSize, halfSize, halfSize), center, null);
            AxisAlignedBoundingBox aabb = (AxisAlignedBoundingBox) getBoundingVolume(newEntity);
            Vector3f entityBoxMinimum = Vector3f.add(aabb.getMinima(), aabb.getPositionOffset(), null);
            Vector3f entityBoxScaledMinimum =
                new Vector3f(entityBoxMinimum.x * entityScale.x, entityBoxMinimum.y * entityScale.y, entityBoxMinimum.z * entityScale.z);
            Vector3f entityWorldMinimum = Vector3f.add(entityBoxScaledMinimum, entityPos, null);
            Vector3f entityBoxMaximum = Vector3f.add(aabb.getMaxima(), aabb.getPositionOffset(), null);
            Vector3f entityBoxScaledMaximum =
                new Vector3f(entityBoxMaximum.x * entityScale.x, entityBoxMaximum.y * entityScale.y, entityBoxMaximum.z * entityScale.z);
            Vector3f entityWorldMaximum = Vector3f.add(entityBoxScaledMaximum, entityPos, null);
            Debug.printDebug(LOGGER, Debug.DEBUG_ALL,
                String.format("Checking if fits: Entity (%s): (%s,%s), Box(%s,%s)", newEntity, entityWorldMinimum, entityWorldMaximum,
                    nodeMinimum, nodeMaxium));
            entityWorldMinimum.y = 0;
            if (entityWorldMinimum.x <= nodeMaxium.x && entityWorldMinimum.x >= nodeMinimum.x
                && entityWorldMinimum.y <= nodeMaxium.y && entityWorldMinimum.y >= nodeMinimum.y
                && entityWorldMinimum.z <= nodeMaxium.z && entityWorldMinimum.z >= nodeMinimum.z
                && entityWorldMaximum.x <= nodeMaxium.x && entityWorldMaximum.x >= nodeMinimum.x
                && entityWorldMaximum.y <= nodeMaxium.y && entityWorldMaximum.y >= nodeMinimum.y
                && entityWorldMaximum.z <= nodeMaxium.z && entityWorldMaximum.z >= nodeMinimum.z) {
                Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "check true");
                return true;
            }
        }
        Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "check false");
        return false;
    }

    private boolean isLeaf() {
        return countChildren() == 0;
    }

    private int countChildren() {
        int childCount = 0;
        for (int i = 0; i < 8; i++) {
            if (children[i] != null) {
                childCount++;

            }
        }
        return childCount;
    }

    public Vector3f getNodeWorldRenderingPosition() {
        return Vector3f.sub(center, new Vector3f(0, halfSize, 0), null);
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

    private Vector3f getEntityScale(String entity) {
        if (entityManager.hasComponent(entity, TransformComponent.class)) {
            return ((TransformComponent) entityManager.getComponent(entity, TransformComponent.class)).getScale();
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

    private void setCenter(Vector3f newCenter) {
        this.center = newCenter;
    }

    public OctreeNode getParent() {
        return parent;
    }

    public int traverseTree(Map<Vector3f, Vector3f> boxes, int currentDepth, boolean text) {
        return traverseTree(boxes, currentDepth, text, 0);
    }

    public int traverseTree(Map<Vector3f, Vector3f> boxes, int currentDepth, boolean text, int entityCount) {
        if (text) {
            for (int i = 0; i < currentDepth; i++) {
                System.out.print("\t");
            }

            Debug.printDebug(LOGGER, Debug.DEBUG_ALL,
                "Depth: " + currentDepth + " Center: " + center + "  Size: " + halfSize + "  Entities: " + entities
                    + "  children: " + countChildren());
        }
        boxes.put(getNodeWorldRenderingPosition(), getNodeWorldScale());
        for (int i = 0; i < 8; i++) {
            if (children[i] != null) {
                entityCount += children[i].traverseTree(boxes, currentDepth + 1, text);
            }
        }
        return entityCount + entities.size();
    }

    /**
     * Checks if something intersects the given ray.
     * 
     * @param currentRay to check
     * @param currentCameraPosition for origin
     * @return entities
     */
    public List<String> intersectsRay(Vector3f currentRay, Vector3f currentCameraPosition) {
        List<String> intersecting = new LinkedList<>();
        if (entities.size() == 0 && countChildren() == 0) {
            return intersecting;
        }
        for (String e : entities) {
            float intersectValue =
                Maths.intersects(getEntityPosition(e), getBoundingVolume(e), currentRay,
                    currentCameraPosition);
            if (intersectValue != Float.NaN && intersectValue > 0) {
                intersecting.add(e);
            }
        }
        for (int i = 0; i < 8; i++) {
            if (children[i] != null) {
                float intersectValue = Maths.intersects(children[i].getCenter(), children[i].halfSize, currentRay,
                    currentCameraPosition);
                if (intersectValue != Float.NaN && intersectValue > 0) {
                    Debug.printDebug(LOGGER, Debug.DEBUG_ALL, "SEARCHING IN CHULD");
                    intersecting.addAll(children[i].intersectsRay(currentRay, currentCameraPosition));
                }
            }
        }
        return intersecting;
    }

}
