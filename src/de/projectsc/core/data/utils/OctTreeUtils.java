/*
 * Copyright (C) 2015
 */

package de.projectsc.core.data.utils;

import java.awt.Color;
import java.awt.Graphics;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.structure.OctTree;
import de.projectsc.core.entities.Entity;

/**
 * Some fine methods for OctTrees.
 * 
 * @author Josch Bosch
 */
public final class OctTreeUtils {

    private static final Color[] COLOR_LEVEL =
        new Color[] { Color.RED, Color.GRAY, Color.GREEN, Color.YELLOW, Color.BLUE, Color.CYAN, Color.MAGENTA,
            Color.PINK };

    private OctTreeUtils() {}

    /**
     * Draws an image of the current tree to the given graphics.
     * 
     * @param treeG to draw to.
     * @param tree to draw
     */
    public static void drawImage(Graphics treeG, OctTree<?> tree) {
        int i = 0;
        // drawLevel(i, treeG, tree);

    }

    private static void drawLevel(int i, Graphics treeG, OctTree<Entity> octTree) {
        Vector3f min = octTree.getRegion().getMin();
        Vector3f size = octTree.getRegion().getSize();
        treeG.setColor(COLOR_LEVEL[i % COLOR_LEVEL.length]);
        if (i < 10) {
            treeG.drawRect((int) min.x, (int) min.z, (int) size.x, (int) size.z);
            for (Entity e : octTree.entities) {
                //
                // Vector3f minBB = Vector3f.add(e.getPosition(), e.getBoundingBox().getMin(), null);
                // Vector3f centerBB = Vector3f.add(e.getPosition(), e.getBoundingBox().getCenter(), null);
                //
                // Vector3f sizeBB = e.getBoundingBox().getSize();
                // treeG.setColor(COLOR_LEVEL[i % COLOR_LEVEL.length]);
                // treeG.fillRect((int) minBB.x, (int) minBB.z, (int) sizeBB.x, (int) sizeBB.z);
                // treeG.setColor(Color.WHITE);
                // treeG.drawOval((int) (centerBB.x - 2), (int) (centerBB.z - 2) - 3, 4, 4);
                // if (octTree.getIntersectionList().contains(e)) {
                // treeG.drawRect((int) minBB.x, (int) minBB.z, (int) sizeBB.x, (int) sizeBB.z);
                // }

            }
        }
        for (int g = 0; g < 8; g++) {
            if (octTree.children[g] != null && octTree.children[g] != octTree) {
                drawLevel(i + 1, treeG, octTree.children[g]);
            }
        }
    }
}
