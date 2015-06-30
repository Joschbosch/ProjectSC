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
import de.projectsc.core.WorldEntity;

public class OctTree {

    private Queue<WorldEntity> pendingEntities = new LinkedList<WorldEntity>();

    private Map<Integer, WorldEntity> entities = new TreeMap<Integer, WorldEntity>();

    private boolean treeBuild = false;

    private BoundingBox region;

    private OctTree parent;

    private OctTree[] children;

    private byte activeChildren = 0;

    private LinkedList<Float[]> interSectionList;

    public OctTree() {
        region = new BoundingBox(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));

    }

    public OctTree(BoundingBox region) {
        this.region = region;
    }

    private OctTree(BoundingBox region, Map<Integer, WorldEntity> entities) {
        this.region = region;
        this.entities = entities;
    }

    public void addEntity(WorldEntity e) {
        pendingEntities.add(e);
    }

    public void updateTree() {
        if (!treeBuild) {
            while (!pendingEntities.isEmpty()) {
                WorldEntity e = pendingEntities.remove();
                entities.put(e.getID(), e);
            }
            buildTree();
        } else {
            while (!pendingEntities.isEmpty()) {
                WorldEntity e = pendingEntities.remove();
                entities.put(e.getID(), e);
                insert(e);
            }
        }
        treeBuild = true;
    }

    private void insert(WorldEntity e) {
        if (entities.size() <= 1 && activeChildren == 0) {
            entities.put(e.getID(), e);
            return;
        }
        Vector3f dimension = region.getSize();

        if (dimension.x <= 1 && dimension.y <= 1 && dimension.z <= 1) {
            entities.put(e.getID(), e);
            return;
        }
        Vector3f half = new Vector3f(dimension.x / 2.0f, dimension.y / 2.0f, dimension.z / 2.0f);
        Vector3f center = Vector3f.sub(region.getMin(), half, null);

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
                entities.put(e.getID(), e);
            }
        }

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
        Vector3f center = Vector3f.sub(region.getMin(), half, null);

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
            if (!e.getBoundingBox().getMin().equals(e.getBoundingBox().getMax())) {
                for (int i = 0; i < 8; i++) {
                    if (containsEntity(octant[i], e)) {
                        subEntities.get(i).add(e);
                        remove.add(e);
                        break;
                    }
                }
            }
        }
        for (WorldEntity e : remove) {
            entities.remove(e.getID());
        }

        for (int i = 0; i < 8; i++) {
            if (!subEntities.get(i).isEmpty()) {
                children[i] = createNewNode(octant[i], subEntities.get(i));
                activeChildren |= (byte) (1 << i);
                children[i].buildTree();
            }
        }
    }

    private boolean containsEntity(BoundingBox boundingBox, WorldEntity e) {

        return false;
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
        return returnTree;
    }

    public void update(List<WorldEntity> movedObjects) {
        if (treeBuild) {
            for (int i = 0; i < 8; i++) {
                if (children[i] != null) {
                    children[i].update(movedObjects);
                }
            }

            for (WorldEntity e : movedObjects) {
                OctTree current = this;
                while (!containsEntity(current.region, e)) {
                    if (current.parent != null) {
                        current = current.parent;
                    } else {
                        break;
                    }
                }
                entities.remove(e.getID());
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
}
