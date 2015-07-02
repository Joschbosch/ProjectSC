/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.entities.MovingEntity;
import de.projectsc.core.entities.WorldEntity;
import de.projectsc.core.utils.BoundingBox;
import de.projectsc.core.utils.OctTree;

public class Tests {

    public static void main(String[] args) {
        WorldEntity e =
            new MovingEntity("goat", "white.png", new Vector3f(100, 0, 100), new Vector3f(0, 0, 0), 1);
        WorldEntity f =
            new MovingEntity("goat", "white.png", new Vector3f(300, 0, 300), new Vector3f(0, 0, 0), 1);
        OctTree<WorldEntity> tree = new OctTree<WorldEntity>(new BoundingBox(new Vector3f(0, 0, 0), new Vector3f(500, 500, 500)));
        tree.addEntity(e);
        tree.addEntity(f);
        tree.recalculateTree();

        System.out.println(tree.toString());
        e.getPosition().x = 300;
        e.getPosition().z = 300;
        ((MovingEntity) e).setMoved(true);
        tree.update();
        tree.update();
        WorldEntity g =
            new MovingEntity("goat", "white.png", new Vector3f(450, 0, 450), new Vector3f(0, 0, 0), 1);
        tree.addEntity(g);
        tree.recalculateTree();
        System.out.println();
        System.out.println();
        System.out.println(tree.toString());
    }
}
