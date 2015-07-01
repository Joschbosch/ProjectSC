/*
 * Copyright (C) 2015
 */

package de.projectsc;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.EntityType;
import de.projectsc.core.WorldEntity;
import de.projectsc.core.utils.BoundingBox;
import de.projectsc.core.utils.OctTree;

public class Tests {

    public static void main(String[] args) {
        WorldEntity e =
            new WorldEntity(EntityType.MOVEABLE_OBJECT, "goat", "white.png", new Vector3f(100, 0, 100), new Vector3f(0, 0, 0), 1);
        WorldEntity f =
            new WorldEntity(EntityType.MOVEABLE_OBJECT, "goat", "white.png", new Vector3f(300, 0, 300), new Vector3f(0, 0, 0), 1);
        OctTree tree = new OctTree(new BoundingBox(new Vector3f(0, 0, 0), new Vector3f(500, 500, 500)));
        tree.addEntity(e);
        tree.addEntity(f);
        tree.recalculateTree();

        System.out.println(tree.toString());
        e.getPosition().x = 300;
        e.getPosition().z = 300;
        e.setMoved(true);
        tree.update();
        tree.update();
        WorldEntity g =
            new WorldEntity(EntityType.MOVEABLE_OBJECT, "goat", "white.png", new Vector3f(450, 0, 450), new Vector3f(0, 0, 0), 1);
        tree.addEntity(g);
        tree.recalculateTree();
        System.out.println();
        System.out.println();
        System.out.println(tree.toString());
    }
}
