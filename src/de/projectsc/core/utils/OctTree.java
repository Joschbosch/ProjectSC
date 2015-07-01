/*
 * Copyright (C) 2015
 */

package de.projectsc.core.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.BoundingBox;
import de.projectsc.core.EntityType;
import de.projectsc.core.WorldEntity;

public class OctTree {

    private final Queue<WorldEntity> pendingEntities = new LinkedList<WorldEntity>();

    private Map<Integer, WorldEntity> entities = new TreeMap<Integer, WorldEntity>();

    private boolean treeBuild = false;

    private final BoundingBox region;

    private OctTree parent;

    private OctTree[] children;

    private byte activeChildren = 0;

    private LinkedList<Float[]> interSectionList;

    public OctTree() {
        region = new BoundingBox(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));

    }

    public OctTree(BoundingBox region) {
        this.region = region;
        children = new OctTree[8];
    }

    private OctTree(BoundingBox region, Map<Integer, WorldEntity> entities) {
        this.region = region;
        this.entities = entities;
        children = new OctTree[8];
    }

    public void addEntity(WorldEntity e) {
        pendingEntities.add(e);
    }

    public void updateTree() {
        if (!treeBuild) {
            while (!pendingEntities.isEmpty()) {
                WorldEntity e = pendingEntities.remove();
                entities.put(new Integer(e.getID()), e);
            }
            buildTree();
        } else {
            while (!pendingEntities.isEmpty()) {
                WorldEntity e = pendingEntities.remove();
                insert(e);
            }
        }
        treeBuild = true;
    }

    private void insert(WorldEntity e) {
        System.out.println("insert " + hashCode());
        if (entities.size() <= 1 && activeChildren == 0) {
            entities.put(new Integer(e.getID()), e);
            return;
        }
        Vector3f dimension = region.getSize();

        if (dimension.x <= 1 && dimension.y <= 1 && dimension.z <= 1) {
            entities.put(new Integer(e.getID()), e);
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
                        List<WorldEntity> list = new LinkedList<>();
                        list.add(e);
                        children[i] = createNewNode(octant[i], list);
                        activeChildren |= (byte) (1 << i);
                    }

                    foundChild = true;
                }
            }
            if (!foundChild) {
                System.out.println("not found put1" + this.hashCode());
                entities.put(new Integer(e.getID()), e);
            }
        }
        treeBuild = true;

    }

    private void buildTree() {
        if (entities.size() <= 1) {
            return;
        }
        Vector3f dimension = region.getSize();

        if (dimension.lengthSquared() == 0.0) {

        }
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

        Map<Integer, List<WorldEntity>> subEntities = new TreeMap<>();
        for (int i = 0; i < 8; i++) {
            subEntities.put(i, new LinkedList<>());
        }

        List<WorldEntity> remove = new LinkedList<>();
        for (WorldEntity e : entities.values()) {
            // only boxes for now.
            for (int i = 0; i < 8; i++) {
                if (containsEntity(octant[i], e)) {
                    subEntities.get(i).add(e);
                    remove.add(e);
                    break;
                }
            }
        }
        for (WorldEntity e : remove) {
            entities.remove(new Integer(e.getID()));
        }

        for (int i = 0; i < 8; i++) {
            if (!subEntities.get(i).isEmpty()) {
                children[i] = createNewNode(octant[i], subEntities.get(new Integer(i)));
                activeChildren |= (byte) (1 << i);
                children[i].buildTree();
            }
        }
    }

    private boolean containsEntity(BoundingBox boundingBox, WorldEntity e) {
        Vector3f minBB = Vector3f.add(e.getPosition(), e.getBoundingBox().getMin(), null);
        Vector3f maxBB = Vector3f.add(e.getPosition(), e.getBoundingBox().getMax(), null);
        if (minBB.x < boundingBox.getMax().x && minBB.x > boundingBox.getMin().x
            && minBB.y < boundingBox.getMax().y && minBB.y > boundingBox.getMin().y
            && minBB.z < boundingBox.getMax().z && minBB.z > boundingBox.getMin().z
            && maxBB.x < boundingBox.getMax().x && maxBB.x > boundingBox.getMin().x
            && maxBB.y < boundingBox.getMax().y && maxBB.y > boundingBox.getMin().y
            && maxBB.z < boundingBox.getMax().z && maxBB.z > boundingBox.getMin().z) {
            return true;
        }
        return false;
    }

    private boolean boundingBoxesColliding(WorldEntity a, WorldEntity b) {
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

    private OctTree createNewNode(BoundingBox boundingBox, List<WorldEntity> list) {
        if (list.isEmpty()) {
            return null;
        }
        Map<Integer, WorldEntity> map = new HashMap<>();
        for (WorldEntity e : list) {
            map.put(e.getID(), e);
        }
        OctTree returnTree = new OctTree(boundingBox, map);
        returnTree.parent = this;
        returnTree.treeBuild = true;

        return returnTree;
    }

    public void update() {
        if (treeBuild) {

            List<WorldEntity> movedObjects = new LinkedList<>();
            for (WorldEntity e : entities.values()) {
                if (e.getType() == EntityType.MOVEABLE_OBJECT || e.getType() == EntityType.PLAYER) {
                    if (e.hasMoved()) {
                        movedObjects.add(e);
                    }
                }
            }

            for (int i = 0; i < 8; i++) {
                if (children[i] != null) {
                    children[i].update();
                }
            }
            List<WorldEntity> toRemove = new LinkedList<>();
            for (WorldEntity e : movedObjects) {
                OctTree current = this;
                while (!containsEntity(current.region, e)) {
                    if (current.parent != null) {
                        current = current.parent;
                    } else {
                        break;
                    }

                }

                entities.remove(new Integer(e.getID()));
                current.insert(e);
            }
            // root node
            if (parent != null) {
                interSectionList = new LinkedList<Float[]>();
                getAllIntersections(interSectionList);
            }

        }
    }

    public List<Float[]> getAllIntersections(LinkedList<Float[]> interSectionList2) {
        return new LinkedList<Float[]>();
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
}
