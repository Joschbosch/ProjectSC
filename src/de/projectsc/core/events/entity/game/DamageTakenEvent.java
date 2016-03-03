/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.events.entity.game;

import de.projectsc.core.data.EntityEvent;

public class DamageTakenEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = DamageTakenEvent.class.getName();

    private String attacker;

    private double damage;

    private double newHealth;

    public DamageTakenEvent(String entityID, String attacker, double newHealth, double damage) {
        super(ID, entityID);
        this.attacker = attacker;
        this.newHealth = newHealth;
        this.damage = damage;
    }

    public String getAttacker() {
        return attacker;
    }

    public double getDamage() {
        return damage;
    }

    public double getNewHealth() {
        return newHealth;
    }

}
