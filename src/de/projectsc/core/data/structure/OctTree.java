/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.data.structure;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.physics.BoundingBox;
import de.projectsc.core.data.physics.PhysicalObject;

/**
 * Data structure for finding collisions of {@link PhysicalObject}s.
 * 
 * @param <T> extends {@link PhysicalObject}
 * @author Josch Bosch
 */
public class OctTree<T extends PhysicalObject> {

    private static final int MAXIMUM_LIFESPAN = 64;

    private static final int LIFE_INIT_VALUE = -1;

    /**
     * entities of the current node.
     */
    public List<T> entities = new LinkedList<>();

    /**
     * Children of the current node.
     */
    public final OctTree<T>[] children;

    private final Queue<T> pendingEntities = new LinkedList<>();

    private boolean treeBuild = false;

    private final BoundingBox region;

    private OctTree<T> parent;

    private List<T> interSectionList;

    private int maxLifespan = 8; //

    private int curLife = 0 - 1;

    private byte activeNodes = 0;

    @SuppressWarnings("unchecked")
    public OctTree(BoundingBox region) {
        this.region = region;
        children = new OctTree[8];
    }

    @SuppressWarnings("unchecked")
    private OctTree(BoundingBox region, List<T> entities) {
        this.region = region;
        this.entities = entities;
        children = new OctTree[8];
    }

    /**
     * Add new entity to OcTree.
     * 
     * @param e to add
     */
    public void addEntity(T e) {
        pendingEntities.add(e);
    }

    /**
     * Recalculate the whole tree.
     */
    public void recalculateTree() {
        if (!treeBuild) {
            while (!pendingEntities.isEmpty()) {
                T e = pendingEntities.remove();
                entities.add(e);
            }
            buildTree();
        } else {
            while (!pendingEntities.isEmpty()) {
                T e = pendingEntities.remove();
                insert(e);
            }
        }
        treeBuild = true;
    }

    private void insert(T e) {
        if (entities.size() <= 1 && activeNodes == 0) {
            entities.add(e);
            return;
        }
        Vector3f dimension = region.getSize();
        if (dimension.x <= 1 && dimension.y <= 1 && dimension.z <= 1) {
            entities.add(e);
            return;
        }
        Vector3f half = new Vector3f(dimension.x / 2.0f, dimension.y / 2.0f, dimension.z / 2.0f);
        Vector3f center = Vector3f.add(region.getMin(), half, null);

        BoundingBox[] octant = new BoundingBox[8];
        createOctantWithChildren(center, octant);
        if (containsEntity(region, e)) {
            boolean foundChild = false;
            for (int i = 0; i < 8; i++) {
                if (containsEntity(octant[i], e)) {
                    if (children[i] != null) {
                        children[i].insert(e);
                    } else {
                        List<T> list = new LinkedList<>();
                        list.add(e);
                        children[i] = createNewNode(octant[i], list);
                        activeNodes |= (byte) (1 << i);
                    }

                    foundChild = true;
                }
            }
            if (!foundChild) {
                entities.add(e);
            }
        } else {
            buildTree();
        }
    }

    private void createOctantWithChildren(Vector3f center, BoundingBox[] octant) {
        octant[0] = children[0] != null ? children[0].region : new BoundingBox(region.getMin(), center);
        octant[1] = children[1] != null ? children[1].region
            : new BoundingBox(new Vector3f(center.x, region.getMin().y, region.getMin().z), new Vector3f(region.getMax().x, center.y,
                center.z));
        octant[2] = children[2] != null ? children[2].region
            : new BoundingBox(new Vector3f(center.x, region.getMin().y, center.z), new Vector3f(region.getMax().x, center.y,
                region.getMax().z));
        octant[3] = children[3] != null ? children[3].region
            : new BoundingBox(new Vector3f(region.getMin().x, region.getMin().y, center.z), new Vector3f(center.x, center.y,
                region.getMax().z));
        octant[4] = children[4] != null ? children[4].region
            : new BoundingBox(new Vector3f(region.getMin().x, center.y, region.getMin().z), new Vector3f(center.x, region.getMax().y,
                center.z));
        octant[5] =
            children[5] != null ? children[5].region : new BoundingBox(new Vector3f(center.x, center.y, region.getMin().z), new Vector3f(
                region.getMax().x, region.getMax().y,
                center.z));
        octant[6] = children[6] != null ? children[6].region : new BoundingBox(center, region.getMax());
        octant[7] =
            children[7] != null ? children[7].region : new BoundingBox(new Vector3f(region.getMin().x, center.y, center.z), new Vector3f(
                center.x, region.getMax().y, region.getMax().z));
    }

    private void buildTree() {
        if (entities.size() <= 1) {
            return;
        }
        Vector3f dimension = region.getSize();
        if (dimension.x <= 1 && dimension.y <= 1 && dimension.z <= 1) {
            return;
        }
        Vector3f half = new Vector3f(dimension.x / 2.0f, dimension.y / 2.0f, dimension.z / 2.0f);
        Vector3f center = Vector3f.add(region.getMin(), half, null);
        BoundingBox[] octant = new BoundingBox[8];
        createOctant(center, octant);

        Map<Integer, List<T>> subEntities = new TreeMap<>();
        for (int i = 0; i < 8; i++) {
            subEntities.put(i, new LinkedList<>());
        }

        List<T> remove = new LinkedList<>();
        for (T e : entities) {
            // only boxes for now.
            for (int i = 0; i < 8; i++) {
                if (containsEntity(octant[i], e)) {
                    subEntities.get(i).add(e);
                    remove.add(e);
                    break;
                }
            }
        }
        for (T e : remove) {
            entities.remove(e);
        }

        for (int i = 0; i < 8; i++) {
            if (!subEntities.get(i).isEmpty()) {
                children[i] = createNewNode(octant[i], subEntities.get(i));
                activeNodes |= (byte) (1 << i);
                children[i].buildTree();
            }
        }
        treeBuild = true;
    }

    private void createOctant(Vector3f center, BoundingBox[] octant) {
        octant[0] = new BoundingBox(region.getMin(), center);
        octant[1] =
            new BoundingBox(new Vector3f(center.x, region.getMin().y, region.getMin().z), new Vector3f(region.getMax().x, center.y,
                center.z));
        octant[2] =
            new BoundingBox(new Vector3f(center.x, region.getMin().y, center.z), new Vector3f(region.getMax().x, center.y,
                region.getMax().z));
        octant[3] =
            new BoundingBox(new Vector3f(region.getMin().x, region.getMin().y, center.z), new Vector3f(center.x, center.y,
                region.getMax().z));
        octant[4] =
            new BoundingBox(new Vector3f(region.getMin().x, center.y, region.getMin().z), new Vector3f(center.x, region.getMax().y,
                center.z));
        octant[5] =
            new BoundingBox(new Vector3f(center.x, center.y, region.getMin().z), new Vector3f(region.getMax().x, region.getMax().y,
                center.z));
        octant[6] = new BoundingBox(center, region.getMax());
        octant[7] =
            new BoundingBox(new Vector3f(region.getMin().x, center.y, center.z), new Vector3f(center.x, region.getMax().y,
                region.getMax().z));
    }

    private boolean containsEntity(BoundingBox boundingBox, T e) {
        Vector3f minBB = Vector3f.add(e.getPosition(), e.getBoundingBox().getMin(), null);
        Vector3f maxBB = Vector3f.add(e.getPosition(), e.getBoundingBox().getMax(), null);
        if (minBB.x <= boundingBox.getMax().x && minBB.x >= boundingBox.getMin().x
            && minBB.y <= boundingBox.getMax().y && minBB.y >= boundingBox.getMin().y
            && minBB.z <= boundingBox.getMax().z && minBB.z >= boundingBox.getMin().z
            && maxBB.x <= boundingBox.getMax().x && maxBB.x >= boundingBox.getMin().x
            && maxBB.y <= boundingBox.getMax().y && maxBB.y >= boundingBox.getMin().y
            && maxBB.z <= boundingBox.getMax().z && maxBB.z >= boundingBox.getMin().z) {
            return true;
        }
        return false;
    }

    private boolean boundingBoxesColliding(T a, T b) {
        Vector3f aMax = Vector3f.add(a.getPosition(), a.getBoundingBox().getMax(), null);
        Vector3f aMin = Vector3f.add(a.getPosition(), a.getBoundingBox().getMin(), null);
        Vector3f bMax = Vector3f.add(b.getPosition(), b.getBoundingBox().getMax(), null);
        Vector3f bMin = Vector3f.add(b.getPosition(), b.getBoundingBox().getMin(), null);
        return (aMax.x > bMin.x
            && aMin.x < bMax.x
            && aMax.y > bMin.y
            && aMin.y < bMax.y
            && aMax.z > bMin.z
            && aMin.z < bMax.z);
    }

    private OctTree<T> createNewNode(BoundingBox boundingBox, List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        OctTree<T> returnTree = new OctTree<T>(boundingBox, list);
        returnTree.parent = this;
        return returnTree;
    }

    private boolean hasChildren() {
        for (int i = 0; i < 8; i++) {
            if (children[i] != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Update tree after moving the objects.
     */
    public void update() {
        // if (treeBuild) {
        if (entities.size() == 0) {
            if (!hasChildren()) {
                if (curLife == LIFE_INIT_VALUE) {
                    curLife = maxLifespan;
                } else if (curLife > 0) {
                    curLife--;
                }
            }
        } else {
            if (curLife != LIFE_INIT_VALUE) {
                if (maxLifespan <= MAXIMUM_LIFESPAN) {
                    maxLifespan *= 2;
                }
                curLife = LIFE_INIT_VALUE;
            }
        }

        List<T> movedObjects = new LinkedList<>();
        for (T e : entities) {
            if (e.isMovable() && e.hasMoved()) {
                movedObjects.add(e);
            }
        }

        int listSize = entities.size();
        List<T> remove = new LinkedList<>();
        for (int i = 0; i < listSize; i++) {
            if (entities.get(i) == null) {
                if (movedObjects.contains(entities.get(i))) {
                    movedObjects.remove(entities.get(i));
                }
                remove.add(entities.get(i));
            }
        }
        for (T e : remove) {
            movedObjects.remove(e);
        }
        for (int i = 0; i < 8; i++) {
            if (children[i] != null) {
                children[i].update();
            }
        }
        for (T e : movedObjects) {
            OctTree<T> current = this;
            while (!containsEntity(current.region, e)) {
                if (current.parent != null) {
                    current = current.parent;
                } else {
                    break;
                }
            }
            entities.remove(e);
            current.insert(e);
        }

        for (int flags = activeNodes, index = 0; flags > 0; flags >>= 1, index++) {
            if ((flags & 1) == 1 && children[index].curLife == 0) {
                children[index] = null;
                activeNodes ^= (byte) (1 << index);
            }
        }
        if (parent == null) {
            interSectionList = getAllIntersections(new LinkedList<T>());
        }
    }

    public List<T> getIntersectionList() {
        return interSectionList;
    }

    private List<T> getAllIntersections(LinkedList<T> parentEntities) {
        List<T> intersectionIDs = new LinkedList<>();
        for (T parentEntity : parentEntities) {
            for (T entity : entities) {
                if (parentEntity.getBoundingBox() != null
                    && entity.getBoundingBox() != null && boundingBoxesColliding(parentEntity, entity)) {
                    if (parentEntity.isMovable()) {
                        intersectionIDs.add(parentEntity);
                    }
                    if (entity.isMovable()) {
                        intersectionIDs.add(entity);
                    }
                }
            }
        }
        if (entities.size() > 1) {
            PriorityQueue<T> tmp = new PriorityQueue<>(entities);
            while (!tmp.isEmpty()) {
                T current = tmp.remove();
                for (T e : entities) {
                    if (e.isMovable() || current.isMovable()) {
                        if (current.getBoundingBox() != null && (e.getBoundingBox() != null && boundingBoxesColliding(current, e))) {
                            if (e.isMovable()) {
                                intersectionIDs.add(e);
                            }
                            if (current.isMovable()) {
                                intersectionIDs.add(current);
                            }
                        }
                    }
                }
            }
            for (T e : entities) {
                if (e.isMovable()) {
                    parentEntities.add(e);
                }
            }
            for (int i = 0; i < 8; i++) {
                if (children[i] != null) {
                    intersectionIDs.addAll(children[i].getAllIntersections(parentEntities));
                }
            }
        }
        return intersectionIDs;
    }

    public List<T> getEntities() {
        return entities;
    }

    public BoundingBox getRegion() {
        return region;
    }

}
