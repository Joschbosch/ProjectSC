/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import de.projectsc.core.utils.GraphEdge;
import de.projectsc.core.utils.GraphNode;

/**
 * One tile of the map.
 *
 * @author Josch Bosch
 */
public class Tile extends GraphNode {

    /**
     * Not walkable tile.
     */
    public static final byte NOT_WALKABLE = 2;

    /**
     * Can become walkable later.
     */
    public static final byte LATER_WALKABLE = 1;

    /**
     * Tile is walkable.
     */
    public static final byte WALKABLE = 0;

    private final Vector2f coordinates;

    private List<GraphEdge> neighbors;

    private final byte height;

    private byte walkable;

    private final byte type;

    public Tile(Vector2f coordinates, byte height, byte walkable, byte type) {
        this.height = height;
        this.walkable = walkable;
        this.coordinates = coordinates;
        this.type = type;
        neighbors = new LinkedList<>();
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

    @Override
    public int hashCode() {
        return super.hashCode();
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

    @Override
    public boolean isWalkable() {
        return walkable == WALKABLE;
    }

    public void setWalkable(byte i) {
        this.walkable = i;
    }

    @Override
    public String toString() {
        String result = "";
        result += "coordinates: " + coordinates + ";";
        result += "walkable: " + walkable + ";";
        return result;
    }

}
