/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class AStar<T extends GraphNode> {

    private List<AStarNode<T>> openList;

    private Set<T> closeList;

    public Queue<T> calculatePath(T start, T target) {
        openList = new LinkedList<>();
        closeList = new HashSet<>();
        openList.add(new AStarNode<T>(start, 0.0f, 0.0f));

        while (!openList.isEmpty()) {
            AStarNode<T> current = openList.remove(0);
            if (current.getNode().equals(target)) {
                return getPath(current);
            }
            closeList.add(current.getNode());
            expandNode(current, target);
        }

        return null;
    }

    private void expandNode(AStarNode<T> current, T target) {
        for (GraphEdge e : ((GraphNode) current.getNode()).getAllNeighbors()) {
            if (!closeList.contains(e.getTarget())) {
                Float newCosts = current.getCost() + e.getCost();
                AStarNode<T> successor = new AStarNode(e.getTarget(), 0f, 0f);
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

class AStarNode<T> implements Comparable<AStarNode<T>> {

    private Float cost;

    private T node;

    private AStarNode<T> previous;

    private Float priority;

    public AStarNode(T node, Float cost, Float priority) {
        this.node = node;
        this.cost = cost;
        this.priority = priority;
        this.previous = null;
    }

    public void setPriority(Float f) {
        priority = f;
    }

    public void setCost(Float newCosts) {
        cost = newCosts;
    }

    public AStarNode(T node, Float cost, Float priority, AStarNode<T> previous) {
        this.node = node;
        this.cost = cost;
        this.priority = priority;
        this.previous = previous;
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

    @Override
    public boolean equals(Object other) {
        if (other instanceof AStarNode) {
            return node.equals(((AStarNode<T>) other).getNode());
        }
        return super.equals(other);
    }
}
