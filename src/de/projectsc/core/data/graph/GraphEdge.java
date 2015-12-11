/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.data.graph;

/**
 * An Edge in the graph.
 * 
 * @author Josch Bosch
 */
public class GraphEdge {

    private GraphNode source;

    private GraphNode target;

    private float cost;

    public GraphEdge(GraphNode source, GraphNode target, float cost) {
        this.source = source;
        this.target = target;
        this.cost = cost;
    }

    public GraphNode getSource() {
        return source;
    }

    public void setSource(GraphNode source) {
        this.source = source;
    }

    public GraphNode getTarget() {
        return target;
    }

    @Override
    public String toString() {
        String result = "";
        result += source + " -> " + target;
        return result;
    }

    public void setTarget(GraphNode target) {
        this.target = target;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

}
