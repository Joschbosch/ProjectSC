/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.utils;

import java.util.List;

public abstract class GraphNode {

    public abstract List<GraphEdge> getAllNeighbors();

    public abstract Float getHeuristikCostsTo(GraphNode target);

    public abstract boolean equals(GraphNode other);
}
