/*
 * Copyright (C) 2015 
 */

package de.projectsc.core;

import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import de.projectsc.core.utils.GraphEdge;
import de.projectsc.core.utils.GraphNode;

public class Tile extends GraphNode {

    public static byte NOT_WALKABLE = 2;

    public static byte LATER_WALKABLE = 1;

    public static byte WALKABLE = 0;

    private Vector2f coordinates;

    private List<GraphEdge> neighbors;

    private final byte height;

    private byte walkable;

    private byte type;

    public Tile(Vector2f coordinates, byte height, byte walkable, byte type) {
        this.height = height;
        this.walkable = walkable;
        this.coordinates = coordinates;
        this.type = type;
    }

    @Override
    public List<GraphEdge> getAllNeighbors() {
        return neighbors;
    }

    @Override
    public Float getHeuristikCostsTo(GraphNode target) {
        Vector2f distance = Vector2f.sub(coordinates, ((Tile) target).getCoordinates(), null);
        return distance.length();
    }

    public Vector2f getCoordinates() {
        return coordinates;
    }

    @Override
    public boolean equals(GraphNode other) {
        return this.coordinates.equals(((Tile) other).getCoordinates());
    }

    public List<GraphEdge> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(List<GraphEdge> neighbors) {
        this.neighbors = neighbors;
    }

    public byte getHeight() {
        return height;
    }

    public byte getWalkAble() {
        return walkable;
    }

    public byte getType() {
        return type;
    }

}
