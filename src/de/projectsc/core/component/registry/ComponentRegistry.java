/*
 * Copyright (C) 2015
 */

package de.projectsc.core.component.registry;

import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.component.collision.ColliderComponent;
import de.projectsc.core.component.physic.MeshComponent;
import de.projectsc.core.component.physic.PathComponent;
import de.projectsc.core.component.physic.TransformComponent;
import de.projectsc.core.component.physic.VelocityComponent;
import de.projectsc.core.component.state.ControlableComponent;
import de.projectsc.core.component.state.EntityStateComponent;
import de.projectsc.core.game.components.AffiliationComponent;
import de.projectsc.core.game.components.BasicAttackComponent;
import de.projectsc.core.game.components.HealthComponent;
import de.projectsc.core.game.components.ProjectileComponent;
import de.projectsc.modes.client.game.component.JumpingComponent;
import de.projectsc.modes.server.game.ai.AIControlledComponent;
import de.projectsc.modes.server.game.ai.FollowPathComponent;
import de.projectsc.modes.server.game.ai.OverwatchComponent;
import de.projectsc.modes.server.game.ai.PathPointComponent;
import de.projectsc.modes.server.game.elementComponents.SpawnEntitiesComponent;

/**
 * These are all (non GUI) components that are known in the engine.
 *
 * @author Josch Bosch
 */
public enum ComponentRegistry {
    /**
     * Component for movement.
     */
    VELOCITY_COMPONENT(VelocityComponent.NAME, VelocityComponent.class, true),
    /**
     * Represents the state of an entity.
     */
    ENTITY_STATE_COMPONENT(EntityStateComponent.NAME, EntityStateComponent.class, false),
    /**
     * The mesh of a component (which might not be used only in the GUI).
     */
    MESH_COMPONENT(MeshComponent.NAME, MeshComponent.class, true),
    /**
     * 
     */
    COLLIDER_COMPONENT(ColliderComponent.NAME, ColliderComponent.class, true),
    /**
     *
     */
    TRANSFORM_COMPONENT(TransformComponent.NAME, TransformComponent.class, false),
    /**
    *
    */
    PATH_COMPONENT(PathComponent.NAME, PathComponent.class, false),
    /**
    * 
    */
    JUMPING_COMPONENT(JumpingComponent.NAME, JumpingComponent.class, true),
    /**
    * 
    */
    HEALTH_COMPONENT(HealthComponent.NAME, HealthComponent.class, true),
    /**
     * 
     */
    AFFILIATION_COMPONENT(AffiliationComponent.NAME, AffiliationComponent.class, true),
    /**
      * 
      */
    ATTACK_COMPONENT(BasicAttackComponent.NAME, BasicAttackComponent.class, true),
    /**
     * 
     */
    OVERWATCH_COMPONENT(OverwatchComponent.NAME, OverwatchComponent.class, true),
    /**
     * 
     */
    PROJECTILE_COMPONENT(ProjectileComponent.NAME, ProjectileComponent.class, true),
    /**
     * 
     */
    CONTROLABLE_COMPONENT(ControlableComponent.NAME, ControlableComponent.class, true),
    /**
     * 
     */
    FOLLOW_PATH_COMPONENT(FollowPathComponent.NAME, FollowPathComponent.class, true),
    /**
     * 
     */
    PATH_POINT_COMPONENT(PathPointComponent.NAME, PathPointComponent.class, true),
    /**
     * 
     */
    SPAWN_ENTITIES_COMPONENT(SpawnEntitiesComponent.NAME, SpawnEntitiesComponent.class, true),
    /**
     * 
     */
    AI_CONTROLLED_COMPONENT(AIControlledComponent.NAME, AIControlledComponent.class, true);

    private String name;

    private Class<? extends DefaultComponent> clazz;

    private boolean addAble;

    ComponentRegistry(String name, Class<? extends DefaultComponent> clazz, boolean addAble) {
        this.setName(name);
        this.setClazz(clazz);
        this.addAble = addAble;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<? extends DefaultComponent> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends DefaultComponent> clazz) {
        this.clazz = clazz;
    }

    public boolean isAddAble() {
        return addAble;
    }
}
