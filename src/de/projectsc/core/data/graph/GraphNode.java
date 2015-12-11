/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.data.graph;

import java.util.List;

/**
 * Node class for all classes that need to be used with the A* algorithm.
 * 
 * @author Josch Bosch
 */
public abstract class GraphNode {

    /**
     * Get list with all neighbors for the current node.
     * 
     * @return list
     */
    public abstract List<GraphEdge> getAllNeighbors();

    /**
     * Estimate costs to target node.
     * 
     * @param target for calculation
     * @return cost
     */
    public abstract Float getHeuristikCostsTo(GraphNode target);

    /**
     * Override.
     * 
     * @param other to compare
     * @return true, if nodes are the same
     */
    public abstract boolean equals(GraphNode other);

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Check if node is walkable.
     * 
     * @return true if it is
     */
    public abstract boolean isWalkable();

    @Override
    public abstract String toString();
}
