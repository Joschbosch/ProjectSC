/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.game.components;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.interfaces.Component;

public class BasicAttackComponent extends DefaultComponent {

    /**
     * ID.
     */
    public static final String NAME = "Basic Attack Component";

    /**
     * Range for a melee attack.
     */
    public static final double MELEE_ATTACK_RANGE = 11;

    private double basicAttackDamage = 10;

    private double basicAttackDuration = 1500;

    private double basicAttackDamageTime = 1000;

    private double basicAttackRange = 50; // < x = Melee

    private long attackTime = 0;

    private String target;

    private boolean damageApplied;

    private long rangedAttackMissleID = 10005;

    public BasicAttackComponent() {
        setComponentName(NAME);
        setType(ComponentType.GAME);
    }

    @Override
    public Component cloneComponent() {
        BasicAttackComponent bac = new BasicAttackComponent();
        bac.setAttackTime(attackTime);
        bac.setDamageApplied(damageApplied);
        bac.setRangedAttackMissleID(rangedAttackMissleID);
        bac.setTarget(target);
        bac.setBasicAttackDamage(basicAttackDamage);
        bac.setBasicAttackDamageTime(basicAttackDamageTime);
        bac.setBasicAttackDuration(basicAttackDuration);
        bac.setBasicAttackRange(basicAttackRange);
        return bac;
    }

    public void setBasicAttackDamage(double basicAttackDamage) {
        this.basicAttackDamage = basicAttackDamage;
    }

    public void setBasicAttackDuration(double basicAttackDuration) {
        this.basicAttackDuration = basicAttackDuration;
    }

    public void setBasicAttackDamageTime(double basicAttackDamageTime) {
        this.basicAttackDamageTime = basicAttackDamageTime;
    }

    public void setBasicAttackRange(double basicAttackRange) {
        this.basicAttackRange = basicAttackRange;
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

    public long getRangedAttackMissleID() {
        return rangedAttackMissleID;
    }

    public void setRangedAttackMissleID(long rangedAttackMissleID) {
        this.rangedAttackMissleID = rangedAttackMissleID;
    }

}
