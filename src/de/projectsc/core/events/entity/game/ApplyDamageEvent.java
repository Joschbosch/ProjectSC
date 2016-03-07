/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.events.entity.game;

import de.projectsc.core.data.EntityEvent;

/**
 * Event that damage should be applied.
 * 
 * @author Josch Bosch
 */
public class ApplyDamageEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = ApplyDamageEvent.class.getName();

    private String target;

    private double damage;

    public ApplyDamageEvent(String entityID, String target, double damage) {
        super(ID, entityID);
        this.setTarget(target);
        this.damage = damage;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public double getDamage() {
        return damage;
    }
}
