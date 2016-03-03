/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.game.components;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;

public class BasicAttackComponent extends DefaultComponent {

    /**
     * ID.
     */
    public static final String NAME = "Basic Attack Component";

    private double basicAttackDamage = 10;

    private double basicAttackDuration = 1500;

    private double basicAttackDamageTime = 1000;

    private double basicAttackRange = 10;

    private long attackTime = 0;

    private String target;

    private boolean damageApplied;

    public BasicAttackComponent() {
        setComponentName(NAME);
        setType(ComponentType.GAME);
    }

    @Override
    public boolean isValidForSaving() {
        return true;
    }

    public double getBasicAttackDamage() {
        return basicAttackDamage;
    }

    public double getBasicAttackDuration() {
        return basicAttackDuration;
    }

    public double getBasicAttackRange() {
        return basicAttackRange;
    }

    public void setAttackTime(long time) {
        this.attackTime = time;

    }

    public void setTarget(String attackingEntityId) {
        this.target = attackingEntityId;
    }

    public double getBasicAttackDamageTime() {
        return basicAttackDamageTime;
    }

    public long getAttackTime() {
        return attackTime;
    }

    public String getTarget() {
        return target;
    }

    public boolean isDamageApplied() {
        return damageApplied;
    }

    public void setDamageApplied(boolean damageApplied) {
        this.damageApplied = damageApplied;
    }

}
