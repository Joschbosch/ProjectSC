/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package testing;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.entities.MovingEntity;
import de.projectsc.core.entities.WorldEntity;
import de.projectsc.core.utils.BoundingBox;
import de.projectsc.core.utils.GraphEdge;
import de.projectsc.core.utils.GraphNode;
import de.projectsc.core.utils.OctTree;

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
        // Terrain t = TerrainLoader.loadTerrain("newDataMap.psc");
        // t.buildNeighborhood();
        // for (WorldEntity e : t.getStaticObjects().values()) {
        // if (e.getBoundingBox() != null) {
        // Vector3f realMinPos = Vector3f.add(e.getBoundingBox().getMin(), e.getPosition(), null);
        // Vector3f realMaxPos = Vector3f.add(e.getBoundingBox().getMax(), e.getPosition(), null);
        // if (realMinPos.x > 0 && realMinPos.z > 0) {
        // realMinPos = (Vector3f) realMinPos.scale(1.0f / Terrain.TERRAIN_TILE_SIZE);
        // realMaxPos = (Vector3f) realMaxPos.scale(1.0f / Terrain.TERRAIN_TILE_SIZE);
        // for (int i = (int) realMinPos.x; i < (int) realMaxPos.x; i++) {
        // for (int j = (int) realMinPos.z; j < (int) realMaxPos.z; j++) {
        // if (t.getTerrain()[i][j] != null) {
        // t.getTerrain()[i][j].setWalkable(Tile.NOT_WALKABLE);
        // }
        // }
        // }
        // }
        // }
        // }
        // t.getTerrain()[4][4].setWalkable(Tile.NOT_WALKABLE);
        // t.getTerrain()[5][3].setWalkable(Tile.NOT_WALKABLE);
        // t.getTerrain()[3][5].setWalkable(Tile.NOT_WALKABLE);
        // t.getTerrain()[5][2].setWalkable(Tile.NOT_WALKABLE);
        // t.getTerrain()[5][1].setWalkable(Tile.NOT_WALKABLE);
        // t.getTerrain()[5][0].setWalkable(Tile.NOT_WALKABLE);
        // t.getTerrain()[1][5].setWalkable(Tile.NOT_WALKABLE);
        // t.getTerrain()[2][5].setWalkable(Tile.NOT_WALKABLE);
        // t.getTerrain()[3][5].setWalkable(Tile.NOT_WALKABLE);
        //
        // t.getTerrain()[4][5].setWalkable(Tile.NOT_WALKABLE);
        //
        // t.getTerrain()[5][4].setWalkable(Tile.NOT_WALKABLE);
        // for (int i = 0; i < t.getMapSize(); i++) {
        // for (int j = 0; j < t.getMapSize(); j++) {
        // if (t.getTerrain()[j][i] != null) {
        // System.out.print(t.getTerrain()[j][i].getWalkAble());
        // } else {
        // System.out.print(" ");
        // }
        //
        // }
        // System.out.println();
        // }
        // TerrainLoader.createBlendMap(t);
        WorldEntity e =
            new MovingEntity("goat", "white.png", new Vector3f(100, 0, 100), new Vector3f(0, 0, 0),
                1);
        WorldEntity f =
            new MovingEntity("goat", "white.png", new Vector3f(300, 0, 300), new Vector3f(0, 0, 0),
                1);
        OctTree<WorldEntity> tree = new OctTree<WorldEntity>(new BoundingBox(new Vector3f(0, 0,
            0), new Vector3f(500, 500, 500)));
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
            new MovingEntity("goat", "white.png", new Vector3f(450, 0, 450), new Vector3f(0, 0, 0),
                1);
        tree.addEntity(g);
        tree.recalculateTree();
        System.out.println();
        System.out.println();
        System.out.println(tree.toString());
        tree.drawImage(new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB).getGraphics());
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

        // AStar<Tile> aStar = new AStar<>();
        // System.out.println("11: " + t.getTerrain()[1][1]);
        // Tile start = t.getTerrain()[1][1];
        // Tile target = t.getTerrain()[6][6];
        // Queue<Tile> path = aStar.getPath(start, target);
        //
        // if (path != null) {
        // for (Tile c : path) {
        // System.out.println("Next path point: ");
        // System.out.println(c.getCoordinates());
        // System.out.println(c.isWalkable());
        // System.out.println(c.getHeuristikCostsTo(target));
        //
        // }
        // } else {
        // System.out.println("NO PATH");
        // }
    }
}

class City extends GraphNode {

    private final List<GraphEdge> neighbors;

    private final float distanceToTarget;

    private final String name;

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

    @Override
    public boolean isWalkable() {
        return true;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }
}
