/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Algorithm to calculate the shortest distance between two nodes.
 * 
 * @param <T> for all kinds of graphs.
 * @author Josch Bosch
 */
public class AStar<T extends GraphNode> {

    private List<AStarNode<T>> openList;

    private Set<T> closeList;

    /**
     * Calculate path to target.
     * 
     * @param start node
     * @param target node
     * @return path nodes
     */
    public Queue<T> getPath(T start, T target) {
        Queue<T> rawPath = calculatePath(start, target);
        Queue<T> newPath = new LinkedList<>();
        T checkPoint = rawPath.remove();
        newPath.add(checkPoint);
        T currentPoint = rawPath.remove();
        while (rawPath.peek() != null) {
            if (walkable(currentPoint, rawPath.peek())) {
                currentPoint = rawPath.remove();
            } else {
                checkPoint = currentPoint;
                currentPoint = rawPath.remove();
                newPath.add(checkPoint);

            }
        }
        return newPath;
    }

    private boolean walkable(T currentPoint, T peek) {
        return true;
    }

    private Queue<T> calculatePath(T start, T target) {
        openList = new LinkedList<>();
        closeList = new HashSet<>();
        AStarNode<T> startAStar = new AStarNode<T>(start, 0.0f, Float.MAX_VALUE);
        openList.add(startAStar);
        AStarNode<T> leastPriority = startAStar;
        while (!openList.isEmpty()) {
            AStarNode<T> current = openList.remove(0);
            if (current.getNode().equals(target)) {
                return getPath(current);
            }
            if (leastPriority.getPriority() > current.getPriority()) {
                leastPriority = current;
            }
            closeList.add(current.getNode());
            expandNode(current, target);
        }

        return getPath(leastPriority);
    }

    @SuppressWarnings("unchecked")
    private void expandNode(AStarNode<T> current, T target) {
        for (GraphEdge e : ((GraphNode) current.getNode()).getAllNeighbors()) {
            if (!closeList.contains(e.getTarget())) {
                if (e.getTarget().isWalkable()) {
                    Float newCosts = current.getCost() + e.getCost();
                    AStarNode<T> successor = new AStarNode<T>((T) e.getTarget(), 0f, 0f);
                    if (openList.contains(successor)) {
                        AStarNode<T> existingSuccessor = null;
                        for (int i = 0; i < openList.size(); i++) {
                            if (openList.get(i).equals(successor)) {
                                existingSuccessor = openList.get(i);
                            }
                        }
                        if (newCosts < existingSuccessor.getCost()) {
                            existingSuccessor.setPrevious(current);
                            existingSuccessor.setCost(newCosts);
                        }
                        existingSuccessor.setPriority(newCosts + existingSuccessor.getNode().getHeuristikCostsTo(target));
                    } else {
                        openList.add(new AStarNode<T>((T) e.getTarget(), newCosts, newCosts + e.getTarget().getHeuristikCostsTo(target),
                            current));
                    }
                }
            }
        }
        Collections.sort(openList);
    }

    private Queue<T> getPath(AStarNode<T> current) {
        Queue<T> path = new LinkedList<>();
        AStarNode<T> pathNode = current;
        while (pathNode.getPrevious() != null) {
            path.add(pathNode.getNode());
            pathNode = pathNode.getPrevious();
        }
        path.add(pathNode.getNode());
        Collections.reverse((List<?>) path);
        return path;
    }
}

/**
 * Node in the A* algorithm.
 * 
 * @param <T>
 * @author Josch Bosch
 */
class AStarNode<T> implements Comparable<AStarNode<T>> {

    private Float cost;

    private final T node;

    private AStarNode<T> previous;

    private Float priority;

    public AStarNode(T graphNode, Float cost, Float priority) {
        this.node = graphNode;
        this.cost = cost;
        this.priority = priority;
        this.previous = null;
    }

    public AStarNode(T node, Float cost, Float priority, AStarNode<T> previous) {
        this.node = node;
        this.cost = cost;
        this.priority = priority;
        this.previous = previous;
    }

    public Float getPriority() {
        return priority;
    }

    public void setPriority(Float f) {
        priority = f;
    }

    public void setCost(Float newCosts) {
        cost = newCosts;
    }

    public T getNode() {
        return node;
    }

    public Float getCost() {
        return cost;
    }

    public void setPrevious(AStarNode<T> previous) {
        this.previous = previous;
    }

    public AStarNode<T> getPrevious() {
        return previous;
    }

    @Override
    public int compareTo(AStarNode<T> o) {
        return priority.compareTo(o.priority);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object other) {
        if (other instanceof AStarNode) {
            return node.equals(((AStarNode<T>) other).getNode());
        }
        return super.equals(other);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
