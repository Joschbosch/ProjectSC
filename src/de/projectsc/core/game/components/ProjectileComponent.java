/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.game.components;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.game.ProjectileType;
import de.projectsc.core.interfaces.Component;
/**
 * Component that defines an entity as projectile.
 * @author Josch Bosch
 */
public class ProjectileComponent extends DefaultComponent {
    /**
     * Component name.
     */
    public static final String NAME = "Projectile Component";

    // configuration members

    private ProjectileType projectileType = ProjectileType.LOCKED_TARGET;

    private float speed = 60f;

    // instance members

    private String target;

    private Vector3f lastLocationOfTarget;

    private String shooter;

    private double damage;

    public ProjectileComponent() {
        setComponentName(NAME);
        setType(ComponentType.GAME);
    }

    @Override
    public Component cloneComponent() {
        ProjectileComponent pc = new ProjectileComponent();
        pc.setProjectileType(projectileType);
        return pc;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Vector3f getLastLocationOfTarget() {
        return lastLocationOfTarget;
    }

    public void setLastLocationOfTarget(Vector3f lastLocationOfTarget) {
        this.lastLocationOfTarget = lastLocationOfTarget;
    }

    public void setShooter(String shooter) {
        this.shooter = shooter;
    }

    public String getShooter() {
        return shooter;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double basicAttackDamage) {
        this.damage = basicAttackDamage;
    }

    public ProjectileType getProjectileType() {
        return projectileType;
    }

    public void setProjectileType(ProjectileType type) {
        this.projectileType = type;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
