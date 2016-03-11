/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.systems.physics.collision;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.data.physics.boundings.AxisAlignedBoundingBox;

public class OctreeTestClass {

    public static void main(String[] args) {
        OctTree2<String> testTree = new OctTree2<>(new AxisAlignedBoundingBox(new Vector3f(),
            new Vector3f((float) Math.pow(2, 5), (float) Math.pow(2, 5), (float) Math.pow(2, 5))));

        Transform t1 = new Transform();
        t1.setPosition(new Vector3f(50, 50, 50));
        AxisAlignedBoundingBox vol1 = new AxisAlignedBoundingBox(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
        testTree.addEntity("testi1", t1, vol1);
        testTree.recalculateTree();

        Transform t2 = new Transform();
        t2.setPosition(new Vector3f(1, 1, 1));
        AxisAlignedBoundingBox vol2 = new AxisAlignedBoundingBox(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
        testTree.addEntity("testi2", t2, vol2);
        testTree.recalculateTree();

        for (int i = 0; i < 99; i++) {
            Transform t = new Transform();
            t.setPosition(new Vector3f(i, 0, i));
            AxisAlignedBoundingBox vol = new AxisAlignedBoundingBox(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
            testTree.addEntity("test" + i, t, vol);
        }
        testTree.recalculateTree();
        testTree.update(new LinkedList<>());
        System.out.println(testTree.getIntersectionList());
        // testTree.drawImage(testTree.toString(img.getGraphics()));
        for (int i = 0; i < 99; i++) {
            testTree.removeEntity("test" + i);
        }
        testTree.recalculateTree();

        t1.setPosition(new Vector3f(10, 0, 10));
        List<String> moved = new LinkedList<>();
        moved.add("testi1");
        testTree.update(moved);
        testTree.update(moved);

        testTree.update(moved);
        testTree.update(moved);

        testTree.update(moved);
        testTree.update(moved);
        System.out.println(testTree.toString());
        System.out.println(testTree.getIntersectionList());
    }
}
