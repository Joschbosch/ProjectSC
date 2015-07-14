/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.core.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;

import org.lwjgl.util.vector.Vector3f;

/**
 * Data structure for finding collisions of {@link PhysicalObject}s.
 * 
 * @param <T> extends {@link PhysicalObject}
 * @author Josch Bosch
 */
public class OctTree<T extends PhysicalObject> {

    private static final int MAXIMUM_LIFESPAN = 64;

    private static final int LIFE_INIT_VALUE = -1;

    private final Queue<T> pendingEntities = new LinkedList<>();

    private final Color[] colorLevel =
        new Color[] { Color.RED, Color.GRAY, Color.GREEN, Color.YELLOW, Color.BLUE, Color.CYAN, Color.MAGENTA,
            Color.PINK };

    private List<T> entities = new LinkedList<>();

    private boolean treeBuild = false;

    private final BoundingBox region;

    private OctTree<T> parent;

    private final OctTree<T>[] children;

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

        // root node
        if (parent == null) {
            interSectionList = getAllIntersections(new LinkedList<T>());
        }

        // }
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

    @Override
    public String toString() {
        String result = "OcTree node: " + this.hashCode() + " \n";
        result += "Entities#: " + entities.size() + "\n";
        result += "Entities: " + entities + "\n";
        result += "Region: " + region.toString() + "\n";
        result += "pending entities: " + pendingEntities + "\n";

        result += "treeBuild: " + treeBuild + "\n";
        // result += "parent: " + parent == null ? parent.hashCode() : "null" + "\n";
        int count = 0;
        for (int i = 0; i < 8; i++) {
            if (children != null && children[i] != null) {
                count++;
            }
        }
        result += "children count: " + count + "\n";
        for (int i = 0; i < 8; i++) {
            if (children != null && children[i] != null) {
                result += "child " + i + " : " + children[i].hashCode() + "\n";
                result += children[i];
            }
        }
        return result;
    }

    /**
     * Draws an image of the current tree to the given graphics.
     * 
     * @param treeG to draw to.
     */
    public void drawImage(Graphics treeG) {
        int i = 0;
        drawLevel(i, treeG, this);

    }

    private void drawLevel(int i, Graphics treeG, OctTree<T> octTree) {

        Vector3f min = octTree.region.getMin();
        Vector3f size = octTree.region.getSize();
        treeG.setColor(colorLevel[i % colorLevel.length]);
        if (i < 10) {
            treeG.drawRect((int) min.x, (int) min.z, (int) size.x, (int) size.z);
            for (T e : octTree.entities) {

                Vector3f minBB = Vector3f.add(e.getPosition(), e.getBoundingBox().getMin(), null);
                Vector3f centerBB = Vector3f.add(e.getPosition(), e.getBoundingBox().getCenter(), null);

                Vector3f sizeBB = e.getBoundingBox().getSize();
                treeG.setColor(colorLevel[i % colorLevel.length]);
                treeG.fillRect((int) minBB.x, (int) minBB.z, (int) sizeBB.x, (int) sizeBB.z);
                treeG.setColor(Color.WHITE);
                treeG.drawOval((int) (centerBB.x - 2), (int) (centerBB.z - 2) - 3, 4, 4);
                if (interSectionList.contains(e)) {
                    treeG.drawRect((int) minBB.x, (int) minBB.z, (int) sizeBB.x, (int) sizeBB.z);
                }

            }
        }
        for (int g = 0; g < 8; g++) {
            if (octTree.children[g] != null && octTree.children[g] != octTree) {
                drawLevel(i + 1, treeG, octTree.children[g]);
            }
        }
    }
}
