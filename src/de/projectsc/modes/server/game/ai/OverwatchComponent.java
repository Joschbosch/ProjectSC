/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.server.game.ai;

import java.util.LinkedList;
import java.util.List;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.interfaces.Component;

/**
 * Component that adds an overwatch to the entity. Other entities that come in range are registered.
 * 
 * @author Josch Bosch
 */
public class OverwatchComponent extends DefaultComponent {

    /**
     * ID.
     */
    public static final String NAME = "Overwatch Component";

    private double radius = 10;

    private boolean useBasicAttackRange = true;

    private List<String> entitesInRange = new LinkedList<>();

    public OverwatchComponent() {
        setType(ComponentType.PREPHYSICS);
        setComponentName(NAME);
        getRequiredComponents().add("Basic Attack Component");
    }

    @Override
    public Component cloneComponent() {
        OverwatchComponent oc = new OverwatchComponent();
        oc.setRadius(radius);
        oc.setUseBasicAttackRange(useBasicAttackRange);
        return oc;
    }

    @Override
    public boolean isValidForEntitySaving() {
        return true;
    }

    /**
     * Check if another entity is in range.
     * 
     * @param otherEntityId to check
     * @return true, if it is.
     */
    public boolean isInRange(String otherEntityId) {
        return entitesInRange.contains(otherEntityId);
    }

    /**
     * Add an entity that came in range.
     * 
     * @param otherEntityId to add
     */
    public void addEntityInRange(String otherEntityId) {
        entitesInRange.add(otherEntityId);
    }

    /**
     * Remove entity that went out of range.
     * 
     * @param otherEntityId to remove
     */
    public void removeOtherEntity(String otherEntityId) {
        entitesInRange.remove(otherEntityId);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public List<String> getEntitiesInRange() {
        return entitesInRange;
    }
    /**
     * Use the basic attack range as radius for the overwatch. 
     * @return true if it should be used.
     */
    public boolean useBasicAttackRange() {
        return useBasicAttackRange;
    }

    public void setUseBasicAttackRange(boolean useBasicAttackRange) {
        this.useBasicAttackRange = useBasicAttackRange;
    }

}
