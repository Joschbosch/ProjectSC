/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.systems.physics.collision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.physics.BoundingVolume;
import de.projectsc.core.data.physics.BoundingVolumeType;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.data.physics.boundings.AxisAlignedBoundingBox;
import de.projectsc.core.utils.Maths;

/**
 * Data structure for finding collisions of {@link PhysicalObject}s.
 * 
 * @param <T> extends {@link PhysicalObject}
 * @author Josch Bosch
 */
public class OctTree<T> {

    private static final Log LOGGER = LogFactory.getLog(OctTree.class);

    private static final int MAXIMUM_LIFESPAN = 64;

    private static final int LIFE_INIT_VALUE = -1;

    /**
     * entities of the current node.
     */
    public Map<T, OctTreeEntry<T>> entities = new HashMap<>();

    /**
     * Children of the current node.
     */
    public final OctTree<T>[] children;

    private final Map<T, OctTreeEntry<T>> pendingEntities = new HashMap<>();

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
    private OctTree(AxisAlignedBoundingBox region, Map<T, OctTreeEntry<T>> entities) {
        this.region = region;
        this.entities = entities;
        children = new OctTree[8];
    }

    /**
     * Add new entity to OcTree.
     * 
     * @param e to add
     * @param t Transform of entity
     * @param b simple bounding volume of entity
     */
    public void addEntity(T e, Transform t, BoundingVolume b) {
        pendingEntities.put(e, new OctTreeEntry<T>(e, t, b));
    }

    /**
     * Recalculate the whole tree.
     */
    public void recalculateTree() {
        if (!treeBuild) {
            entities.putAll(pendingEntities);
            pendingEntities.clear();
            buildTree();
        } else {
            for (T entity : pendingEntities.keySet()) {
                insert(entity, pendingEntities.get(entity));
            }
            pendingEntities.clear();
        }
        treeBuild = true;
    }

    private void insert(T e, OctTreeEntry<T> octTreeEntry) {
        if (entities.size() <= 1 && activeNodes == 0) {
            entities.put(e, octTreeEntry);
            return;
        }

        Vector3f dimension = Vector3f.sub(region.getMaxima(), region.getMinima(), null);
        if (dimension.x <= 0.5 && dimension.y <= 0.5 && dimension.z <= 0.5) {
            entities.put(e, octTreeEntry);
            return;
        }

        Vector3f center = Maths.getCenter(region);

        AxisAlignedBoundingBox[] octant = new AxisAlignedBoundingBox[8];
        createOctantWithChildren(center, octant);
        if (containsVolume(region, e, octTreeEntry)) {
            boolean foundChild = false;
            for (int i = 0; i < 8; i++) {
                if (containsVolume(octant[i], e, octTreeEntry)) {
                    if (children[i] != null) {
                        children[i].insert(e, octTreeEntry);
                    } else {
                        Map<T, OctTreeEntry<T>> list = new HashMap<>();
                        list.put(e, octTreeEntry);
                        children[i] = createNewNode(octant[i], list);
                        activeNodes |= (byte) (1 << i);

                    }

                    foundChild = true;
                }
            }
            if (!foundChild) {
                entities.put(e, octTreeEntry);
            }
        } else {
            buildTree();
        }
    }

    private void createOctantWithChildren(Vector3f center, AxisAlignedBoundingBox[] octant) {
        octant[0] = children[0] != null ? children[0].region : new AxisAlignedBoundingBox(region.getMinima(), center);
        octant[1] =
            children[1] != null ? children[1].region
                : new AxisAlignedBoundingBox(new Vector3f(center.x, region.getMinima().y, region.getMinima().z), new Vector3f(
                    region.getMaxima().x,
                    center.y,
                    center.z));
        octant[2] =
            children[2] != null ? children[2].region
                : new AxisAlignedBoundingBox(new Vector3f(center.x, region.getMinima().y, center.z), new Vector3f(region.getMaxima().x,
                    center.y,
                    region.getMaxima().z));
        octant[3] =
            children[3] != null ? children[3].region
                : new AxisAlignedBoundingBox(new Vector3f(region.getMinima().x, region.getMinima().y, center.z), new Vector3f(center.x,
                    center.y,
                    region.getMaxima().z));
        octant[4] =
            children[4] != null ? children[4].region
                : new AxisAlignedBoundingBox(new Vector3f(region.getMinima().x, center.y, region.getMinima().z), new Vector3f(center.x,
                    region.getMaxima().y,
                    center.z));
        octant[5] =
            children[5] != null ? children[5].region : new AxisAlignedBoundingBox(new Vector3f(center.x, center.y, region.getMinima().z),
                new Vector3f(
                    region.getMaxima().x, region.getMaxima().y,
                    center.z));
        octant[6] = children[6] != null ? children[6].region : new AxisAlignedBoundingBox(center, region.getMaxima());
        octant[7] =
            children[7] != null ? children[7].region : new AxisAlignedBoundingBox(new Vector3f(region.getMinima().x, center.y, center.z),
                new Vector3f(
                    center.x, region.getMaxima().y, region.getMaxima().z));
    }

    private void buildTree() {
        if (entities.size() <= 1) {
            return;
        }
        Vector3f dimension = Maths.getSize(region);
        if (dimension.x <= 0.5f && dimension.y <= 0.5f && dimension.z <= 0.5) {
            return;
        }
        AxisAlignedBoundingBox[] octant = new AxisAlignedBoundingBox[8];
        createOctant(Maths.getCenter(region), octant);
        Map<Integer, Map<T, OctTreeEntry<T>>> subEntities = new TreeMap<>();
        for (int i = 0; i < 8; i++) {
            subEntities.put(i, new HashMap<>());
        }

        List<T> remove = new LinkedList<>();
        for (T e : entities.keySet()) {
            // only boxes for now.
            BoundingVolumeType type = entities.get(e).getBoundingVolume().getType();
            if (type == BoundingVolumeType.AXIS_ALIGNED_BOUNDING_BOX) {
                for (int i = 0; i < 8; i++) {
                    if (containsVolume(octant[i], e, entities.get(e))) {
                        subEntities.get(i).put(e, entities.get(e));
                        remove.add(e);
                        break;
                    }
                }
            }
            if (type == BoundingVolumeType.SPHERE) {
                for (int i = 0; i < 8; i++) {
                    if (containsVolume(octant[i], e, entities.get(e))) {
                        subEntities.get(i).put(e, entities.get(e));
                        remove.add(e);
                        break;
                    }
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
        octant[0] = new AxisAlignedBoundingBox(region.getMinima(), center);
        octant[1] =
            new AxisAlignedBoundingBox(new Vector3f(center.x, region.getMinima().y, region.getMinima().z), new Vector3f(
                region.getMaxima().x,
                center.y,
                center.z));
        octant[2] =
            new AxisAlignedBoundingBox(new Vector3f(center.x, region.getMinima().y, center.z), new Vector3f(region.getMaxima().x, center.y,
                region.getMaxima().z));
        octant[3] =
            new AxisAlignedBoundingBox(new Vector3f(region.getMinima().x, region.getMinima().y, center.z), new Vector3f(center.x, center.y,
                region.getMaxima().z));
        octant[4] =
            new AxisAlignedBoundingBox(new Vector3f(region.getMinima().x, center.y, region.getMinima().z), new Vector3f(center.x,
                region.getMaxima().y,
                center.z));
        octant[5] =
            new AxisAlignedBoundingBox(new Vector3f(center.x, center.y, region.getMinima().z), new Vector3f(region.getMaxima().x,
                region.getMaxima().y,
                center.z));
        octant[6] = new AxisAlignedBoundingBox(center, region.getMaxima());
        octant[7] =
            new AxisAlignedBoundingBox(new Vector3f(region.getMinima().x, center.y, center.z), new Vector3f(center.x, region.getMaxima().y,
                region.getMaxima().z));
    }

    private boolean containsVolume(AxisAlignedBoundingBox boundingBox, T e, OctTreeEntry<T> ote) {
        Vector3f minBB =
            Vector3f.add(Vector3f.add(ote.getTransform().getPosition(), ote.getBoundingVolume().getMinima(), null), ote.getBoundingVolume()
                .getPositionOffset(), null);
        Vector3f maxBB =
            Vector3f.add(Vector3f.add(ote.getTransform().getPosition(), ote.getBoundingVolume().getMaxima(), null), ote.getBoundingVolume()
                .getPositionOffset(), null);
        if (minBB.x <= boundingBox.getMaxima().x && minBB.x >= boundingBox.getMinima().x
            && minBB.y <= boundingBox.getMaxima().y && minBB.y >= boundingBox.getMinima().y
            && minBB.z <= boundingBox.getMaxima().z && minBB.z >= boundingBox.getMinima().z
            && maxBB.x <= boundingBox.getMaxima().x && maxBB.x >= boundingBox.getMinima().x
            && maxBB.y <= boundingBox.getMaxima().y && maxBB.y >= boundingBox.getMinima().y
            && maxBB.z <= boundingBox.getMaxima().z && maxBB.z >= boundingBox.getMinima().z) {
            return true;
        }
        return false;
    }

    private boolean boundingBoxesColliding(Transform currentTransform, Transform entityTransform, AxisAlignedBoundingBox currentAABB,
        AxisAlignedBoundingBox entityAABB) {

        Vector3f aMax = Vector3f.add(currentTransform.getPosition(), currentAABB.getMaxima(), null);
        Vector3f aMin = Vector3f.add(currentTransform.getPosition(), currentAABB.getMinima(), null);
        Vector3f bMax = Vector3f.add(entityTransform.getPosition(), entityAABB.getMaxima(), null);
        Vector3f bMin = Vector3f.add(entityTransform.getPosition(), entityAABB.getMinima(), null);

        return (aMax.x > bMin.x
            && aMin.x < bMax.x
            && aMax.y > bMin.y
            && aMin.y < bMax.y
            && aMax.z > bMin.z
            && aMin.z < bMax.z);
    }

    private OctTree<T> createNewNode(AxisAlignedBoundingBox boundingBox, Map<T, OctTreeEntry<T>> list) {
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
     * 
     * @param moved entites
     */
    public void update(Map<T, OctTreeEntry<T>> moved) {
        if (treeBuild) {
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

            for (int i = 0; i < 8; i++) {
                if (children[i] != null) {
                    children[i].update(moved);
                }
            }

            for (T e : moved.keySet()) {
                OctTree<T> current = this;
                while (!containsVolume(current.region, e, moved.get(e))) {
                    if (current.parent != null) {
                        current = current.parent;
                    } else {
                        break;
                    }
                }
                entities.remove(e);
                current.insert(e, moved.get(e));
            }

            for (int flags = activeNodes, index = 0; flags > 0; flags >>= 1, index++) {
                if ((flags & 1) == 1 && children[index].curLife == 0) {
                    children[index] = null;
                    activeNodes ^= (byte) (1 << index);
                }
            }
            if (parent == null) {
                interSectionList = getAllIntersections(new HashMap<T, OctTreeEntry<T>>());
            }
        }
    }

    public List<T> getIntersectionList() {
        return interSectionList;
    }

    private List<T> getAllIntersections(Map<T, OctTreeEntry<T>> parentEntities) {
        List<T> intersectionIDs = new ArrayList<>();
        Map<T, OctTreeEntry<T>> entityCopy = new HashMap<>();
        entityCopy.putAll(entities);
        for (T parentEntity : parentEntities.keySet()) {
            for (T entity : entityCopy.keySet()) {
                if (!parentEntity.equals(entity)) {
                    BoundingVolume bvA = parentEntities.get(parentEntity).getBoundingVolume();
                    BoundingVolume bvB = entityCopy.get(entity).getBoundingVolume();
                    if (bvA.getType() == BoundingVolumeType.AXIS_ALIGNED_BOUNDING_BOX
                        && bvB.getType() == BoundingVolumeType.AXIS_ALIGNED_BOUNDING_BOX) {
                        if (boundingBoxesColliding(parentEntities.get(parentEntity).getTransform(), entityCopy.get(entity).getTransform(),
                            (AxisAlignedBoundingBox) bvA, (AxisAlignedBoundingBox) bvB)) {
                            intersectionIDs.add(parentEntity);
                            intersectionIDs.add(entity);
                        }
                    }
                }
            }
            entityCopy.remove(parentEntity);
        }
        if (entities.size() > 1) {
            Map<T, OctTreeEntry<T>> tmp = new HashMap<>(entities);
            entityCopy = new HashMap<>();
            entityCopy.putAll(entities);
            Iterator<T> it = tmp.keySet().iterator();
            while (it.hasNext()) {
                T current = it.next();
                entityCopy.remove(current);
                for (T e : entityCopy.keySet()) {
                    if (!e.equals(current)) {
                        checkForIntersect(intersectionIDs, current, e, tmp.get(current), entityCopy.get(e));
                    }
                }
                it.remove();

            }
            for (T e : entities.keySet()) {
                parentEntities.put(e, entities.get(e));
            }
            for (int i = 0; i < 8; i++) {
                if (children[i] != null) {
                    intersectionIDs.addAll(children[i].getAllIntersections(parentEntities));
                }
            }
        }
        return intersectionIDs;
    }

    private void checkForIntersect(List<T> intersectionIDs, T current, T e, OctTreeEntry<T> currentEntry, OctTreeEntry<T> eEntry) {
        if (currentEntry.getBoundingVolume().getType() == BoundingVolumeType.AXIS_ALIGNED_BOUNDING_BOX
            && eEntry.getBoundingVolume().getType() == BoundingVolumeType.AXIS_ALIGNED_BOUNDING_BOX) {
            if (boundingBoxesColliding(currentEntry.getTransform(), eEntry.getTransform(),
                (AxisAlignedBoundingBox) currentEntry.getBoundingVolume(), (AxisAlignedBoundingBox) eEntry.getBoundingVolume())) {
                intersectionIDs.add(current);
                intersectionIDs.add(e);
            }
        }
    }

    public Map<T, OctTreeEntry<T>> getEntities() {
        return entities;
    }

    public AxisAlignedBoundingBox getRegion() {
        return region;
    }

    /**
     * Remove entity from tree.
     * 
     * @param l to remove
     */
    public void removeEntity(String l) {
        if (removeRecursive(l)) {
            LOGGER.info("Successfully removed entity from octree: " + l);
            buildTree();
        }
    }

    private boolean removeRecursive(String toRemove) {
        Iterator<T> it = entities.keySet().iterator();
        while (it.hasNext()) {
            if (it.next() == toRemove) {
                it.remove();
                return true;
            }
        }
        for (int i = 0; i < 8; i++) {
            if (children[i] != null) {
                boolean removed = children[i].removeRecursive(toRemove);
                if (removed) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        int count = entities.size();
        int level = 0;
        for (int i = 0; i < 8; i++) {
            if (children[i] != null) {
                count += children[i].toStringLevel(level);
            }
        }
        return "" + count;
    }

    private int toStringLevel(int level) {
        level++;
        int countChildren = 0;
        for (int i = 0; i < 8; i++) {
            if (children[i] != null) {
                countChildren += children[i].toStringLevel(level);
            }
        }
        return entities.size() + countChildren;
    }

    /**
     * Debug method.
     * 
     * @return boxes
     */
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

    /**
     * Checks if something intersects the given ray.
     * 
     * @param currentRay to check
     * @param currentCameraPosition for origin
     * @return entities
     */
    public List<T> intersectsRay(Vector3f currentRay, Vector3f currentCameraPosition) {
        List<T> intersecting = new LinkedList<>();
        if (entities.size() == 0 && !hasChildren()) {
            return intersecting;
        }
        for (T e : entities.keySet()) {
            float intersectValue =
                Maths.intersects(entities.get(e).getTransform().getPosition(), entities.get(e).getBoundingVolume(), currentRay,
                    currentCameraPosition);
            if (intersectValue != Float.NaN && intersectValue > 0) {
                intersecting.add(e);
            }
        }
        for (int i = 0; i < 8; i++) {
            if (children[i] != null) {
                float intersectValue = Maths.intersects(children[i].getRegion().getPositionOffset(), children[i].getRegion(), currentRay,
                    currentCameraPosition);
                if (intersectValue != Float.NaN && intersectValue > 0) {
                    intersecting.addAll(children[i].intersectsRay(currentRay, currentCameraPosition));
                }
            }
        }
        return intersecting;
    }
}
