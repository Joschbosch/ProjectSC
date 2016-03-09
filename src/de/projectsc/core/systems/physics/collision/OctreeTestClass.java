/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.systems.physics.collision;

import java.awt.image.BufferedImage;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.data.physics.boundings.AxisAlignedBoundingBox;

public class OctreeTestClass {

    public static void main(String[] args) {
        AxisAlignedBoundingBox region = new AxisAlignedBoundingBox(new Vector3f(), new Vector3f(100, 100, 100));
        OctTree<String> testTree = new OctTree<>(region);
        System.out.println(testTree.getBoxes());

        Transform t1 = new Transform();
        t1.setPosition(new Vector3f(50, 50, 50));
        AxisAlignedBoundingBox vol1 = new AxisAlignedBoundingBox(new Vector3f(-1, 0, -1), new Vector3f(1, 1, 1));
        testTree.addEntity("test1", t1, vol1);
        testTree.recalculateTree();
        System.out.println(testTree.getBoxes());
        System.out.println(testTree.toString());

        Transform t2 = new Transform();
        t2.setPosition(new Vector3f(1, 1, 1));
        AxisAlignedBoundingBox vol2 = new AxisAlignedBoundingBox(new Vector3f(-1, 0, -1), new Vector3f(1, 1, 1));
        testTree.addEntity("test2", t2, vol2);
        testTree.recalculateTree();

        for (int i = 0; i < 99; i++) {
            Transform t = new Transform();
            t.setPosition(new Vector3f(i, i, i));
            AxisAlignedBoundingBox vol = new AxisAlignedBoundingBox(new Vector3f(-1, 0, -1), new Vector3f(1, 1, 1));
            testTree.addEntity("test" + i, t, vol);
        }
        testTree.recalculateTree();
        System.out.println(testTree.getBoxes());
        System.out.println(testTree.toString());
        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
        // testTree.drawImage(testTree.toString(img.getGraphics()));
    }
}
