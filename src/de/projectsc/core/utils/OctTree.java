/*
 * Copyright (C) 2015
 */

package de.projectsc.core.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.EntityType;
import de.projectsc.core.WorldEntity;

public class OctTree {

    private final Queue<WorldEntity> pendingEntities = new LinkedList<WorldEntity>();

    private PriorityQueue<WorldEntity> entities = new PriorityQueue<WorldEntity>();

    private boolean treeBuild = false;

    private final BoundingBox region;

    private OctTree parent;

    private OctTree[] children;

    private byte activeChildren = 0;

    private LinkedList<Integer> interSectionList;

    private int m_maxLifespan = 8; //

    private int m_curLife = -1;

    public OctTree() {
        region = new BoundingBox(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));

    }

    public OctTree(BoundingBox region) {
        this.region = region;
        children = new OctTree[8];
    }

    private OctTree(BoundingBox region, PriorityQueue<WorldEntity> entities) {
        this.region = region;
        this.entities = entities;
        children = new OctTree[8];
    }

    public void addEntity(WorldEntity e) {
        pendingEntities.add(e);
    }

    public void recalculateTree() {
        if (!treeBuild) {
            while (!pendingEntities.isEmpty()) {
                WorldEntity e = pendingEntities.remove();
                entities.add(e);
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
        if (entities.size() <= 1 && !hasChildren()) {
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
                        List<WorldEntity> list = new LinkedList<>();
                        list.add(e);
                        children[i] = createNewNode(octant[i], list);
                        activeChildren |= (byte) (1 << i);
                    }

                    foundChild = true;
                }
            }
            if (!foundChild) {
                entities.add(e);
            }
        } else {
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
        for (WorldEntity e : entities) {
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
            entities.remove(e);
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
        PriorityQueue<WorldEntity> map = new PriorityQueue<>();
        for (WorldEntity e : list) {
            map.add(e);
        }
        OctTree returnTree = new OctTree(boundingBox, map);
        returnTree.parent = this;
        returnTree.treeBuild = true;

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

    private int getAllEnties() {
        int number = entities.size();
        for (int i = 0; i < 8; i++) {
            if (children[i] != null) {
                number += children[i].getAllEnties();
            }
        }
        return number;
    }

    public void update() {
        if (treeBuild) {
            if (entities.size() == 0) {
                if (!hasChildren()) {
                    if (m_curLife == -1) {
                        m_curLife = m_maxLifespan;
                    } else if (m_curLife > 0) {
                        m_curLife--;
                    }
                }
            } else {
                if (m_curLife != -1) {
                    if (m_maxLifespan <= 64) {
                        m_maxLifespan *= 2;
                    }
                    m_curLife = -1;
                }
            }

            List<WorldEntity> movedObjects = new LinkedList<>();
            for (WorldEntity e : entities) {
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
            for (WorldEntity e : movedObjects) {
                OctTree current = this;
                if (parent == null) {
                }
                while (!containsEntity(current.region, e)) {
                    if (current.parent != null) {
                        current = current.parent;
                    } else {
                        break;
                    }

                }

                entities.remove(e);
                current.insert(e);
                if (parent == null) {
                }
            }

            for (int i = 0; i < 8; i++) {
                if (children[i] != null && children[i].m_curLife == 0) {
                    children[i] = null;
                }
            }

            // root node
            if (parent == null) {
                interSectionList = new LinkedList<Integer>();
                interSectionList.addAll(getAllIntersections(new LinkedList<WorldEntity>()));
            }

        }
    }

    public List<Integer> getIntersectionList() {
        return interSectionList;
    }

    public List<Integer> getAllIntersections(LinkedList<WorldEntity> parentEntities) {
        List<Integer> intersectionIDs = new LinkedList<>();
        for (WorldEntity parentEntity : parentEntities) {
            for (WorldEntity entity : entities) {
                if (parentEntity.getBoundingBox() != null
                    && entity.getBoundingBox() != null && boundingBoxesColliding(parentEntity, entity)) {
                    System.out.println("Collision!" + parentEntity.getID() + parentEntity.getModel() + "  " + entity.getID()
                        + entity.getModel());
                    if (parentEntity.getType() == EntityType.MOVEABLE_OBJECT || parentEntity.getType() == EntityType.PLAYER) {
                        intersectionIDs.add(parentEntity.getID());
                    }
                    if (entity.getType() == EntityType.MOVEABLE_OBJECT || entity.getType() == EntityType.PLAYER) {
                        intersectionIDs.add(entity.getID());
                    }
                }
            }
        }
        if (entities.size() > 1) {

            PriorityQueue<WorldEntity> tmp = new PriorityQueue<>(entities);
            while (!tmp.isEmpty()) {
                WorldEntity current = tmp.remove();
                for (WorldEntity e : entities) {
                    if (!e.getID().equals(current.getID())
                        && (e.getType() == EntityType.MOVEABLE_OBJECT || e.getType() == EntityType.PLAYER
                            || current.getType() == EntityType.MOVEABLE_OBJECT || e.getType() == EntityType.PLAYER)) {
                        if (current.getBoundingBox() != null && (e.getBoundingBox() != null && boundingBoxesColliding(current, e))) {
                            System.out.println("Collision2!" + e.getID() + e.getModel() + "   " + current.getID() + current.getModel());
                            if (e.getType() == EntityType.MOVEABLE_OBJECT || e.getType() == EntityType.PLAYER) {
                                intersectionIDs.add(e.getID());
                            }
                            if (current.getType() == EntityType.MOVEABLE_OBJECT || current.getType() == EntityType.PLAYER) {
                                intersectionIDs.add(current.getID());
                            }
                        }
                    }
                }
            }
        }
        for (WorldEntity e : entities) {
            if (e.getType() == EntityType.MOVEABLE_OBJECT || e.getType() == EntityType.PLAYER) {
                parentEntities.add(e);
            }
        }
        for (int i = 0; i < 8; i++) {
            if (children[i] != null) {
                intersectionIDs.addAll(children[i].getAllIntersections(parentEntities));
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
}
