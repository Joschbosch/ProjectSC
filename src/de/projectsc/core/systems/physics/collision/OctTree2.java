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
 * @param <T> generic
 * @author Josch Bosch
 */
public class OctTree2<T> {

    private static final Log LOGGER = LogFactory.getLog(OctTree2.class);

    private static final int MAXIMUM_LIFESPAN = 64;

    private static final int LIFE_INIT_VALUE = -1;

    /**
     * entities of the current node.
     */
    public final List<T> entities = new LinkedList<>();

    /**
     * Children of the current node.
     */
    public final OctTree2<T>[] children;

    private final Map<T, OctTreeEntry<T>> pendingEntities = new HashMap<>();

    private final Map<T, OctTreeEntry<T>> entryMap;

    private boolean treeBuild = false;

    private final AxisAlignedBoundingBox region;

    private OctTree2<T> parent;

    private List<IntersectionRecord<T>> interSectionList;

    private int maxLifespan = 8;

    private int curLife = LIFE_INIT_VALUE;

    private byte activeNodes = 0;

    private boolean treeDirty;

    @SuppressWarnings("unchecked")
    public OctTree2(AxisAlignedBoundingBox region) {
        this.region = region;
        children = new OctTree2[8];
        entryMap = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    private OctTree2(AxisAlignedBoundingBox region, List<T> entities, Map<T, OctTreeEntry<T>> entryMap) {
        this.region = region;
        this.entities.addAll(entities);
        children = new OctTree2[8];
        this.entryMap = entryMap;
    }

    /**
     * Add new entity to OcTree.
     * 
     * @param e to add
     * @param t Transform of entity
     * @param b simple bounding volume of entity
     */
    public void addEntity(T e, Transform t, BoundingVolume b) {
        if (b != null && Maths.getSize(b).length() > 0) {
            pendingEntities.put(e, new OctTreeEntry<T>(e, t, b));
            treeDirty = true;
        } else {
            LOGGER.error(String.format("Entity %s has no bounding volume. Did not add it to octree.", e));
        }
    }

    /**
     * Recalculate the whole tree.
     */
    public void recalculateTree() {
        if (!treeBuild) {
            entities.addAll(pendingEntities.keySet());
            entryMap.putAll(pendingEntities);
            pendingEntities.clear();
            buildTree();
        } else {
            for (T entity : pendingEntities.keySet()) {
                entryMap.put(entity, pendingEntities.get(entity));
                insert(entity, pendingEntities.get(entity));
            }
            pendingEntities.clear();
        }
        treeDirty = false;
    }

    private void insert(T e, OctTreeEntry<T> octTreeEntry) {
        System.out.println("INSERT");
        if (entities.size() <= 1 && activeNodes == 0) {
            entities.add(e);
            System.out.println("ADD 1 " + e);
            return;
        }

        Vector3f dimension = Vector3f.sub(region.getMaxima(), region.getMinima(), null);
        if (dimension.x <= 0.5 && dimension.y <= 0.5 && dimension.z <= 0.5) {
            entities.add(e);
            System.out.println("ADD 2 " + e);
            return;
        }
        System.out.println(getBoxes());
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
        octant[0] = children[0] != null ? children[0].region : new AxisAlignedBoundingBox(region.getMinima(), center);
        octant[1] = children[1] != null ? children[1].region
            : new AxisAlignedBoundingBox(new Vector3f(center.x, region.getMinima().y, region.getMinima().z), new Vector3f(
                region.getMaxima().x, center.y, center.z));
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
        if (dimension.length() <= 0) {
            region.setMinima(new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE));
            region.setMaxima(new Vector3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE));
            findEnclosingCube();
            dimension = Maths.getSize(region);
        }
        if (dimension.x <= 0.5 && dimension.y <= 0.5 && dimension.z <= 0.5) {
            return;
        }
        AxisAlignedBoundingBox[] octant = new AxisAlignedBoundingBox[8];
        createOctant(Maths.getCenter(region), octant);
        Map<Integer, List<T>> subEntities = new TreeMap<>();
        for (int i = 0; i < 8; i++) {
            subEntities.put(i, new LinkedList<>());
        }

        List<T> remove = new LinkedList<>();
        for (T e : entities) {
            // only boxes for now.
            BoundingVolumeType type = entryMap.get(e).getBoundingVolume().getType();
            if (type == BoundingVolumeType.AXIS_ALIGNED_BOUNDING_BOX) {
                for (int i = 0; i < 8; i++) {
                    if (containsVolume(octant[i], e, entryMap.get(e))) {
                        subEntities.get(i).add(e);
                        remove.add(e);
                        break;
                    }
                }
            }
            if (type == BoundingVolumeType.SPHERE) {
                for (int i = 0; i < 8; i++) {
                    if (containsVolume(octant[i], e, entryMap.get(e))) {
                        subEntities.get(i).add(e);
                        remove.add(e);
                        break;
                    }
                }
            }
        }

        entities.removeAll(remove);

        for (int i = 0; i < 8; i++) {
            if (!subEntities.get(i).isEmpty()) {
                children[i] = createNewNode(octant[i], subEntities.get(i));
                activeNodes |= (byte) (1 << i);
                children[i].buildTree();
            }
        }
        treeBuild = true;
    }

    private void findEnclosingCube() {
        findEnclosingBox();
        Vector3f size = Vector3f.sub(region.getMaxima(), region.getMinima(), null);
        int highX = (int) Math.floor(Math.max(Math.max(size.x, size.y), size.z));
        int i = 0;
        while (Math.pow(2, i) <= highX) {
            i++;
        }
        region.setMaxima(new Vector3f(region.getMinima().x + highX, region.getMinima().y + highX, region.getMinima().z + highX));
        Vector3f center = Maths.getCenter(region);
        System.out.println(center);
        Vector3f size2 = Maths.getSize(region);
        region.setPositionOffset(new Vector3f(center.x, center.y - size2.y / 2, center.z));
    }

    private void findEnclosingBox() {
        Vector3f globalMin = new Vector3f(region.getMinima());
        Vector3f globalMax = new Vector3f(region.getMaxima());

        for (T e : entities) {
            Vector3f localMin = new Vector3f(
                Vector3f.add(entryMap.get(e).getBoundingVolume().getMinima(), entryMap.get(e).getTransform().getPosition(), null));
            Vector3f localMax = new Vector3f(
                Vector3f.add(entryMap.get(e).getBoundingVolume().getMaxima(), entryMap.get(e).getTransform().getPosition(), null));
            if (localMin.x < globalMin.x) {
                globalMin.x = localMin.x;
            }
            if (localMin.y < globalMin.y) {
                globalMin.y = localMin.y;
            }
            if (localMin.z < globalMin.z) {
                globalMin.z = localMin.z;
            }

            if (localMax.x > globalMax.x) {
                globalMax.x = localMax.x;
            }
            if (localMax.y > globalMax.y) {
                globalMax.y = localMax.y;
            }
            if (localMax.z > globalMax.z) {
                globalMax.z = localMax.z;
            }
        }
        region.setMinima(globalMin);
        region.setMaxima(globalMax);

    }

    private void createOctant(Vector3f center, AxisAlignedBoundingBox[] octant) {
        octant[0] = new AxisAlignedBoundingBox(region.getMinima(), center);
        octant[1] =
            new AxisAlignedBoundingBox(new Vector3f(center.x, region.getMinima().y, region.getMinima().z), new Vector3f(
                region.getMaxima().x, center.y, center.z));
        octant[2] =
            new AxisAlignedBoundingBox(new Vector3f(center.x, region.getMinima().y, center.z), new Vector3f(region.getMaxima().x, center.y,
                region.getMaxima().z));
        octant[3] =
            new AxisAlignedBoundingBox(new Vector3f(region.getMinima().x, region.getMinima().y, center.z), new Vector3f(center.x, center.y,
                region.getMaxima().z));
        octant[4] =
            new AxisAlignedBoundingBox(new Vector3f(region.getMinima().x, center.y, region.getMinima().z), new Vector3f(center.x,
                region.getMaxima().y, center.z));
        octant[5] =
            new AxisAlignedBoundingBox(new Vector3f(center.x, center.y, region.getMinima().z), new Vector3f(region.getMaxima().x,
                region.getMaxima().y, center.z));
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
        Vector3f regionMin = Vector3f.add(region.getMinima(), region.getPositionOffset(), null);
        Vector3f regionMax = Vector3f.add(region.getMaxima(), region.getPositionOffset(), null);

        if (minBB.x <= regionMax.x && minBB.x >= boundingBox.getMinima().x
            && minBB.y <= regionMax.y && minBB.y >= regionMin.y
            && minBB.z <= regionMax.z && minBB.z >= regionMin.z
            && maxBB.x <= regionMax.x && maxBB.x >= regionMin.x
            && maxBB.y <= regionMax.y && maxBB.y >= regionMin.y
            && maxBB.z <= regionMax.z && maxBB.z >= regionMin.z) {
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

    private OctTree2<T> createNewNode(AxisAlignedBoundingBox boundingBox, List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        OctTree2<T> returnTree = new OctTree2<T>(boundingBox, list, entryMap);
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
    public void update(List<T> moved) {
        if (treeBuild) {
            if (entities.size() == 0) {
                if (!hasChildren()) {
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

            List<T> movedEntities = new LinkedList<>();
            for (T e : entities) {
                if (moved.contains(e)) {
                    movedEntities.add(e);
                }
            }
            moved.removeAll(movedEntities);

            for (int flags = activeNodes, index = 0; flags > 0; flags >>= 1, index++) {
                if ((flags & 1) == 1) {
                    children[index].update(moved);
                }
            }

            for (T e : movedEntities) {
                OctTree2<T> current = this;
                // UPDATE AABB
                while (!containsVolume(current.region, e, entryMap.get(e))) {
                    if (current.parent != null) {
                        current = current.parent;
                    } else {
                        break;
                    }
                }
                // TODO: Update SPhere
                entities.remove(e);
                current.insert(e, entryMap.get(e));
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

    }

    public List<IntersectionRecord<T>> getIntersectionList() {
        return interSectionList;
    }

    private List<IntersectionRecord<T>> getAllIntersections(List<T> parentEntities) {
        List<IntersectionRecord<T>> intersections = new ArrayList<>();
        List<T> entityCopy = new LinkedList<>();
        entityCopy.addAll(entities);
        for (T parentEntity : parentEntities) {
            for (T entity : entityCopy) {
                if (!parentEntity.equals(entity)) {
                    BoundingVolume bvA = entryMap.get(parentEntity).getBoundingVolume();
                    BoundingVolume bvB = entryMap.get(entity).getBoundingVolume();
                    if (bvA.getType() == BoundingVolumeType.AXIS_ALIGNED_BOUNDING_BOX
                        && bvB.getType() == BoundingVolumeType.AXIS_ALIGNED_BOUNDING_BOX) {
                        if (boundingBoxesColliding(entryMap.get(parentEntity).getTransform(), entryMap.get(entity).getTransform(),
                            (AxisAlignedBoundingBox) bvA, (AxisAlignedBoundingBox) bvB)) {
                            IntersectionRecord<T> ir = new IntersectionRecord<>();
                            ir.setObject1(entity);
                            ir.setObject2(parentEntity);
                            ir.setHasHit(true);
                            if (!intersections.contains(ir)) {
                                intersections.add(ir);
                            }
                        }
                    }
                }
            }
            entityCopy.remove(parentEntity);
        }
        if (entities.size() > 1) {
            List<T> tmp = new LinkedList<>(entities);
            entityCopy = new LinkedList<>();
            entityCopy.addAll(entities);
            Iterator<T> it = tmp.iterator();
            while (it.hasNext()) {
                T current = it.next();
                entityCopy.remove(current);
                for (T e : entityCopy) {
                    if (!e.equals(current)) {
                        checkForIntersect(intersections, current, e, entryMap.get(current), entryMap.get(e));
                    }
                }
                it.remove();

            }
            for (T e : entities) {
                parentEntities.add(e);
            }
            for (int i = 0; i < 8; i++) {
                if (children[i] != null) {
                    intersections.addAll(children[i].getAllIntersections(parentEntities));
                }
            }
        }
        return intersections;
    }

    private void checkForIntersect(List<IntersectionRecord<T>> intersections, T current, T e, OctTreeEntry<T> currentEntry,
        OctTreeEntry<T> eEntry) {
        if (currentEntry.getBoundingVolume().getType() == BoundingVolumeType.AXIS_ALIGNED_BOUNDING_BOX
            && eEntry.getBoundingVolume().getType() == BoundingVolumeType.AXIS_ALIGNED_BOUNDING_BOX) {
            if (boundingBoxesColliding(currentEntry.getTransform(), eEntry.getTransform(),
                (AxisAlignedBoundingBox) currentEntry.getBoundingVolume(), (AxisAlignedBoundingBox) eEntry.getBoundingVolume())) {
                IntersectionRecord<T> ir = new IntersectionRecord<>();
                ir.setObject1(current);
                ir.setObject2(e);
                ir.setHasHit(true);
                if (!intersections.contains(ir)) {
                    intersections.add(ir);
                }
            }
        }
    }

    public List<T> getEntities() {
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
    public void removeEntity(T l) {
        if (removeRecursive(l)) {
            entryMap.remove(l);
            LOGGER.info("Successfully removed entity from octree: " + l);
            buildTree();
        }
    }

    private boolean removeRecursive(T toRemove) {
        if (entities.remove(toRemove)) {
            return true;
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
        for (T e : entities) {
            float intersectValue =
                Maths.intersects(entryMap.get(e).getTransform().getPosition(), entryMap.get(e).getBoundingVolume(), currentRay,
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

    public boolean isDirty() {
        return treeDirty;
    }
}
