/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.data.structure;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.impl.physic.ColliderComponent;
import de.projectsc.core.component.impl.physic.VelocityComponent;
import de.projectsc.core.data.physics.AxisAlignedBoundingBox;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.entities.Entity;
import de.projectsc.core.manager.EntityManager;

/**
 * Data structure for finding collisions of {@link PhysicalObject}s.
 * 
 * @param <T> extends {@link PhysicalObject}
 * @author Josch Bosch
 */
public class OctTree<T extends Entity> {

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

    private final AxisAlignedBoundingBox region;

    private OctTree<T> parent;

    private List<T> interSectionList;

    private int maxLifespan = 8; //

    private int curLife = 0 - 1;

    private byte activeNodes = 0;

    @SuppressWarnings("unchecked")
    public OctTree(AxisAlignedBoundingBox region) {
        this.region = region;
        children = new OctTree[8];
    }

    @SuppressWarnings("unchecked")
    private OctTree(AxisAlignedBoundingBox region, List<T> entities) {
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
        if (entities.size() < 1 && activeNodes == 0) {
            entities.add(e);
            return;
        }

        Vector3f dimension = region.getSize();
        if (dimension.x <= 0.5 && dimension.y <= 0.5 && dimension.z <= 0.5) {
            entities.add(e);
            return;
        }
        Vector3f center = region.getCenter();

        AxisAlignedBoundingBox[] octant = new AxisAlignedBoundingBox[8];
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

    private void createOctantWithChildren(Vector3f center, AxisAlignedBoundingBox[] octant) {
        octant[0] = children[0] != null ? children[0].region : new AxisAlignedBoundingBox(region.getMin(), center);
        octant[1] =
            children[1] != null ? children[1].region
                : new AxisAlignedBoundingBox(new Vector3f(center.x, region.getMin().y, region.getMin().z), new Vector3f(region.getMax().x,
                    center.y,
                    center.z));
        octant[2] = children[2] != null ? children[2].region
            : new AxisAlignedBoundingBox(new Vector3f(center.x, region.getMin().y, center.z), new Vector3f(region.getMax().x, center.y,
                region.getMax().z));
        octant[3] = children[3] != null ? children[3].region
            : new AxisAlignedBoundingBox(new Vector3f(region.getMin().x, region.getMin().y, center.z), new Vector3f(center.x, center.y,
                region.getMax().z));
        octant[4] =
            children[4] != null ? children[4].region
                : new AxisAlignedBoundingBox(new Vector3f(region.getMin().x, center.y, region.getMin().z), new Vector3f(center.x,
                    region.getMax().y,
                    center.z));
        octant[5] =
            children[5] != null ? children[5].region : new AxisAlignedBoundingBox(new Vector3f(center.x, center.y, region.getMin().z),
                new Vector3f(
                    region.getMax().x, region.getMax().y,
                    center.z));
        octant[6] = children[6] != null ? children[6].region : new AxisAlignedBoundingBox(center, region.getMax());
        octant[7] =
            children[7] != null ? children[7].region : new AxisAlignedBoundingBox(new Vector3f(region.getMin().x, center.y, center.z),
                new Vector3f(
                    center.x, region.getMax().y, region.getMax().z));
    }

    private void buildTree() {
        if (entities.size() <= 1) {
            return;
        }
        Vector3f dimension = region.getSize();
        if (dimension.x <= 0.5f && dimension.y <= 0.5f && dimension.z <= 0.5) {
            return;
        }

        Vector3f half = new Vector3f(dimension.x / 2.0f, dimension.y / 2.0f, dimension.z / 2.0f);
        Vector3f center = Vector3f.add(region.getMin(), half, null);
        AxisAlignedBoundingBox[] octant = new AxisAlignedBoundingBox[8];
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

    private void createOctant(Vector3f center, AxisAlignedBoundingBox[] octant) {
        octant[0] = new AxisAlignedBoundingBox(region.getMin(), center);
        octant[1] =
            new AxisAlignedBoundingBox(new Vector3f(center.x, region.getMin().y, region.getMin().z), new Vector3f(region.getMax().x,
                center.y,
                center.z));
        octant[2] =
            new AxisAlignedBoundingBox(new Vector3f(center.x, region.getMin().y, center.z), new Vector3f(region.getMax().x, center.y,
                region.getMax().z));
        octant[3] =
            new AxisAlignedBoundingBox(new Vector3f(region.getMin().x, region.getMin().y, center.z), new Vector3f(center.x, center.y,
                region.getMax().z));
        octant[4] =
            new AxisAlignedBoundingBox(new Vector3f(region.getMin().x, center.y, region.getMin().z), new Vector3f(center.x,
                region.getMax().y,
                center.z));
        octant[5] =
            new AxisAlignedBoundingBox(new Vector3f(center.x, center.y, region.getMin().z), new Vector3f(region.getMax().x,
                region.getMax().y,
                center.z));
        octant[6] = new AxisAlignedBoundingBox(center, region.getMax());
        octant[7] =
            new AxisAlignedBoundingBox(new Vector3f(region.getMin().x, center.y, center.z), new Vector3f(center.x, region.getMax().y,
                region.getMax().z));
    }

    private boolean containsEntity(AxisAlignedBoundingBox boundingBox, T e) {
        Transform t = e.getTransform();
        AxisAlignedBoundingBox aabb = ((ColliderComponent) EntityManager.getComponent(e.getID(), ColliderComponent.class)).getAABB();
        Vector3f minBB = Vector3f.add(t.getPosition(), aabb.getMin(), null);
        Vector3f maxBB = Vector3f.add(t.getPosition(), aabb.getMax(), null);
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
        Transform aT = a.getTransform();
        Transform bT = b.getTransform();
        AxisAlignedBoundingBox aabbA = ((ColliderComponent) EntityManager.getComponent(a.getID(), ColliderComponent.class)).getAABB();
        AxisAlignedBoundingBox aabbB = ((ColliderComponent) EntityManager.getComponent(b.getID(), ColliderComponent.class)).getAABB();

        Vector3f aMax = Vector3f.add(aT.getPosition(), aabbA.getMax(), null);
        Vector3f aMin = Vector3f.add(aT.getPosition(), aabbA.getMin(), null);
        Vector3f bMax = Vector3f.add(bT.getPosition(), aabbB.getMax(), null);
        Vector3f bMin = Vector3f.add(bT.getPosition(), aabbB.getMin(), null);
        return (aMax.x > bMin.x
            && aMin.x < bMax.x
            && aMax.y > bMin.y
            && aMin.y < bMax.y
            && aMax.z > bMin.z
            && aMin.z < bMax.z);
    }

    private OctTree<T> createNewNode(AxisAlignedBoundingBox boundingBox, List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        OctTree<T> returnTree = new OctTree<T>(boundingBox, list);
        returnTree.parent = this;
        return returnTree;
    }

    private boolean hasChildren() {
        for (int i = 0; i < 8; i++) {
            if (children[i] != null && children[i].entities.size() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Update tree after moving the objects.
     */
    public void update(List<T> moved) {
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

        int listSize = entities.size();
        List<T> remove = new LinkedList<>();
        for (int i = 0; i < listSize; i++) {
            if (entities.get(i) == null) {
                if (moved.contains(entities.get(i))) {
                    moved.remove(entities.get(i));
                }
                remove.add(entities.get(i));
            }
        }
        for (T e : remove) {
            moved.remove(e);
        }
        for (int i = 0; i < 8; i++) {
            if (children[i] != null) {
                children[i].update(moved);
            }
        }
        for (T e : moved) {
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
                AxisAlignedBoundingBox parentAABB =
                    ((ColliderComponent) EntityManager.getComponent(parentEntity.getID(), ColliderComponent.class)).getAABB();
                AxisAlignedBoundingBox entityAABB =
                    ((ColliderComponent) EntityManager.getComponent(entity.getID(), ColliderComponent.class)).getAABB();
                if (parentAABB != null
                    && entityAABB != null && boundingBoxesColliding(parentEntity, entity)) {
                    if (EntityManager.hasComponent(parentEntity.getID(), VelocityComponent.class)) {
                        intersectionIDs.add(parentEntity);
                    }
                    if (EntityManager.hasComponent(entity.getID(), VelocityComponent.class)) {
                        intersectionIDs.add(entity);
                    }
                }
            }
        }
        if (entities.size() > 1) {
            Queue<T> tmp = new LinkedList<>(entities);
            while (!tmp.isEmpty()) {
                T current = tmp.remove();
                for (T e : entities) {
                    if (EntityManager.hasComponent(e.getID(), VelocityComponent.class)
                        || EntityManager.hasComponent(current.getID(), VelocityComponent.class)) {
                        AxisAlignedBoundingBox entityAABB =
                            ((ColliderComponent) EntityManager.getComponent(e.getID(), ColliderComponent.class)).getAABB();
                        AxisAlignedBoundingBox currentAABB =
                            ((ColliderComponent) EntityManager.getComponent(current.getID(), ColliderComponent.class)).getAABB();
                        if (currentAABB != null && (entityAABB != null && boundingBoxesColliding(current, e))) {
                            if (EntityManager.hasComponent(e.getID(), VelocityComponent.class)) {
                                intersectionIDs.add(e);
                            }
                            if (EntityManager.hasComponent(current.getID(), VelocityComponent.class)) {
                                intersectionIDs.add(current);
                            }
                        }
                    }
                }
            }
            for (T e : entities) {
                if (EntityManager.hasComponent(e.getID(), VelocityComponent.class)) {
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

    public AxisAlignedBoundingBox getRegion() {
        return region;
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
        buildTree();
    }

    @Override
    public String toString() {
        int count = entities.size();
        for (int i = 0; i < 8; i++) {
            if (children[i] != null) {
                count += Integer.parseInt(children[i].toString());
            }
        }
        return "" + count;
    }

    public List<AxisAlignedBoundingBox> getBoxes() {
        List<AxisAlignedBoundingBox> boxes = new LinkedList<AxisAlignedBoundingBox>();
        boxes.add(region);
        for (int i = 0; i < 8; i++) {
            if (children[i] != null) {
                boxes.addAll(children[i].getBoxes());
            }
        }
        return boxes;
    }
}
