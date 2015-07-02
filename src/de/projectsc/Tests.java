/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc;

import java.util.LinkedList;
import java.util.List;

import de.projectsc.core.TerrainLoader;
import de.projectsc.core.utils.GraphEdge;
import de.projectsc.core.utils.GraphNode;

public class Tests {

    public static void main(String[] args) {
        //
        // Tile[][] tiles = new Tile[256][256];
        //
        // for (int i = 0; i < 256; i++) {
        // for (int j = 0; j < 256; j++) {
        // byte type = 0;
        // if (i > 256 / 2) {
        // type = 1;
        // }
        // byte height = (byte) (i % 3);
        // byte walkable = 0;
        // tiles[i][j] = new Tile(new Vector2f(i, j), height, walkable, type);
        // }
        // }
        //
        // Terrain t = new Terrain(tiles, "", "", "", "", new LinkedList<>(), new HashMap<>());
        // TerrainLoader.storeTerrain(t, "newDataMap.psc");
        TerrainLoader.loadTerrain("newDataMap.psc");
        // WorldEntity e =
        // new MovingEntity("goat", "white.png", new Vector3f(100, 0, 100), new Vector3f(0, 0, 0), 1);
        // WorldEntity f =
        // new MovingEntity("goat", "white.png", new Vector3f(300, 0, 300), new Vector3f(0, 0, 0), 1);
        // OctTree<WorldEntity> tree = new OctTree<WorldEntity>(new BoundingBox(new Vector3f(0, 0, 0), new Vector3f(500, 500, 500)));
        // tree.addEntity(e);
        // tree.addEntity(f);
        // tree.recalculateTree();
        //
        // System.out.println(tree.toString());
        // e.getPosition().x = 300;
        // e.getPosition().z = 300;
        // ((MovingEntity) e).setMoved(true);
        // tree.update();
        // tree.update();
        // WorldEntity g =
        // new MovingEntity("goat", "white.png", new Vector3f(450, 0, 450), new Vector3f(0, 0, 0), 1);
        // tree.addEntity(g);
        // tree.recalculateTree();
        // System.out.println();
        // System.out.println();
        // System.out.println(tree.toString());
        // City saarbuecken = new City("sa", 222);
        // City kaiserslautern = new City("kal", 158);
        // City Frankfurt = new City("ff", 96);
        // City Ludwigshafen = new City("lud", 108);
        // City Karlsruhe = new City("kar", 140);
        // City heilbronn = new City("heil", 87);
        // City wuerzburg = new City("w", 0);
        //
        // saarbuecken.addNeighbor(new GraphEdge(saarbuecken, kaiserslautern, 70));
        // saarbuecken.addNeighbor(new GraphEdge(saarbuecken, Karlsruhe, 145));
        // kaiserslautern.addNeighbor(new GraphEdge(kaiserslautern, Frankfurt, 103));
        // kaiserslautern.addNeighbor(new GraphEdge(kaiserslautern, Ludwigshafen, 53));
        // Karlsruhe.addNeighbor(new GraphEdge(Karlsruhe, heilbronn, 84));
        // heilbronn.addNeighbor(new GraphEdge(heilbronn, wuerzburg, 102));
        // Frankfurt.addNeighbor(new GraphEdge(Frankfurt, wuerzburg, 116));
        // Ludwigshafen.addNeighbor(new GraphEdge(Ludwigshafen, wuerzburg, 183));
        // AStar<City> aStar = new AStar<City>();
        //
        // Queue<City> path = aStar.calculatePath(saarbuecken, wuerzburg);
        // for (City c : path) {
        // System.out.println(c.getName());
        // }
    }
}

class City extends GraphNode {

    private List<GraphEdge> neighbors;

    private float distanceToTarget;

    private String name;

    public City(String name, float distanceToTarget) {
        this.distanceToTarget = distanceToTarget;
        this.name = name;
        neighbors = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    @Override
    public List<GraphEdge> getAllNeighbors() {
        return neighbors;
    }

    public void addNeighbor(GraphEdge e) {
        neighbors.add(e);
    }

    @Override
    public Float getHeuristikCostsTo(GraphNode target) {
        return distanceToTarget;
    }

    @Override
    public boolean equals(GraphNode other) {
        System.out.println("equals!" + getName() + ((City) other).getName());
        return name.equals(((City) other).getName());
    }
}
