/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.server.game.ai;

import java.util.LinkedList;
import java.util.List;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.interfaces.Component;

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

    public boolean isInRange(String otherEntityId) {
        return entitesInRange.contains(otherEntityId);
    }

    public void addEntityInRange(String otherEntityId) {
        entitesInRange.add(otherEntityId);
    }

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

    public boolean useBasicAttackRange() {
        return useBasicAttackRange;
    }

    public void setUseBasicAttackRange(boolean useBasicAttackRange) {
        this.useBasicAttackRange = useBasicAttackRange;
    }

}
