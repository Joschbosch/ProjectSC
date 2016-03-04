/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.game.systems;

import java.util.HashSet;
import java.util.Set;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.physic.TransformComponent;
import de.projectsc.core.component.physic.VelocityComponent;
import de.projectsc.core.component.state.EntityStateComponent;
import de.projectsc.core.data.Event;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.entities.states.EntityStates;
import de.projectsc.core.events.entity.actions.BasicAttackEntityAction;
import de.projectsc.core.events.entity.actions.MoveEntityToTargetAction;
import de.projectsc.core.events.entity.game.ApplyDamageEvent;
import de.projectsc.core.events.entity.state.UpdateEntityStateEvent;
import de.projectsc.core.game.components.BasicAttackComponent;
import de.projectsc.core.game.components.ProjectileComponent;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.DefaultSystem;

public class CombatSystem extends DefaultSystem {

    private static final String NAME = "Combat System";

    public CombatSystem(EntityManager entityManager, EventManager eventManager) {
        super(CombatSystem.NAME, entityManager, eventManager);
        eventManager.registerForEvent(BasicAttackEntityAction.class, this);
        eventManager.registerForEvent(MoveEntityToTargetAction.class, this);
    }

    @Override
    public void update(long tick) {
        Set<String> entities = entityManager.getEntitiesWithComponent(BasicAttackComponent.class);
        for (String e : entities) {
            BasicAttackComponent attCmp = getComponent(e, BasicAttackComponent.class);
            if (hasComponent(e, EntityStateComponent.class)) {
                if (attCmp != null && attCmp.getTarget() != null && hasComponent(attCmp.getTarget(), EntityStateComponent.class)
                    && canAttack(getComponent(attCmp.getTarget(), EntityStateComponent.class).getState())) {
                    calculateBasicAttack(tick, e, attCmp);
                }
            }
            entities = new HashSet<>(entityManager.getEntitiesWithComponent(ProjectileComponent.class));
            for (String entity : entities) {
                ProjectileComponent pc = getComponent(entity, ProjectileComponent.class);
                // may be obsolete with collision detection
                if (hasComponent(pc.getTarget(), TransformComponent.class)) {
                    Transform projectileTransform = getComponent(entity, TransformComponent.class).getTransform();
                    Transform targetTransform = getComponent(pc.getTarget(), TransformComponent.class).getTransform();
                    if (Vector3f.sub(projectileTransform.getPosition(), targetTransform.getPosition(), null).length() < 1) {
                        fireEvent(new ApplyDamageEvent(pc.getTarget(), attCmp.getTarget(), attCmp.getBasicAttackDamage()));
                        entityManager.deleteEntity(entity);
                    }
                } else {
                    entityManager.deleteEntity(entity);
                }
            }
        }
    }

    private boolean canAttack(EntityStates targetState) {
        return targetState != EntityStates.DEAD && targetState != EntityStates.DYING;
    }

    private void calculateBasicAttack(long tick, String e, BasicAttackComponent attCmp) {
        EntityStateComponent stateCmp = getComponent(e, EntityStateComponent.class);
        Transform eTransform = getComponent(e, TransformComponent.class).getTransform();
        Transform targetTransform = getComponent(attCmp.getTarget(), TransformComponent.class).getTransform();
        boolean inAttackRange = Vector3f.sub(eTransform.getPosition(), targetTransform.getPosition(), null).length() < attCmp
            .getBasicAttackRange();
        if (stateCmp.getState() == EntityStates.AUTO_ATTACKING) {
            EntityStateComponent targetState = getComponent(attCmp.getTarget(), EntityStateComponent.class);
            long newAttackTime = attCmp.getAttackTime() + tick;
            if (newAttackTime >= attCmp.getBasicAttackDamageTime() && !attCmp.isDamageApplied()) {
                if (attCmp.getBasicAttackRange() < BasicAttackComponent.MELEE_ATTACK_RANGE) {
                    fireEvent(new ApplyDamageEvent(e, attCmp.getTarget(), attCmp.getBasicAttackDamage()));
                } else {
                    launchBasicAttackRangedMissle(eTransform, targetTransform, e, attCmp.getTarget(), attCmp);
                }
                attCmp.setDamageApplied(true);
            }

            if (newAttackTime >= attCmp.getBasicAttackDuration()) {
                if (targetState.getState() != EntityStates.DEAD) {
                    if (inAttackRange) {
                        newAttackTime -= attCmp.getBasicAttackDuration();
                        attCmp.setDamageApplied(false);
                    } else if (hasComponent(e, VelocityComponent.class)) {
                        fireEvent(new UpdateEntityStateEvent(e, EntityStates.MOVE_TO_BASIC_ATTACK));
                        fireEvent(new MoveEntityToTargetAction(e, targetTransform.getPosition()));
                    } else {
                        fireEvent(new UpdateEntityStateEvent(e, EntityStates.IDLING));
                    }
                } else {
                    fireEvent(new UpdateEntityStateEvent(e, EntityStates.IDLING));
                    attCmp.setTarget(null);
                }
            }
            attCmp.setAttackTime(newAttackTime);
        }

        if (stateCmp.getState() == EntityStates.MOVE_TO_BASIC_ATTACK) {
            if (inAttackRange && (stateCmp.getState() == EntityStates.IDLING || stateCmp.getState() == EntityStates.MOVING)) {
                fireEvent(new UpdateEntityStateEvent(e, EntityStates.AUTO_ATTACKING));
                attCmp.setAttackTime(0);
                attCmp.setDamageApplied(false);
            }

        }

    }

    private void launchBasicAttackRangedMissle(Transform eTransform, Transform targetTransform, String shooter, String target,
        BasicAttackComponent attCmp) {
        String missle = entityManager.createNewEntityFromSchema(attCmp.getRangedAttackMissleID());
        ProjectileComponent pCmp = getComponent(missle, ProjectileComponent.class);
        pCmp.setLastLocationOfTarget(targetTransform.getPosition());
        pCmp.setTarget(target);
        pCmp.setShooter(shooter);
        pCmp.setDamage(attCmp.getBasicAttackDamage());
        VelocityComponent velCmp = getComponent(missle, VelocityComponent.class);
        velCmp.setAcceleration(Float.MAX_VALUE);
        velCmp.setMaximumSpeed(pCmp.getSpeed());
        velCmp.setMaximumTurnSpeed(1500f);
        TransformComponent transCmp = getComponent(missle, TransformComponent.class);
        transCmp.setPosition(eTransform.getPosition());
        transCmp.setRotation(eTransform.getRotation());
        fireEvent(new MoveEntityToTargetAction(missle, targetTransform.getPosition()));

    }

    @Override
    public void processEvent(Event e) {
        if (e instanceof BasicAttackEntityAction) {
            String entityId = ((BasicAttackEntityAction) e).getEntityId();
            String attackedEntityId = ((BasicAttackEntityAction) e).getTargetEntity();
            if (hasComponent(entityId, BasicAttackComponent.class)) {
                Transform entityTransform = getComponent(entityId, TransformComponent.class).getTransform();
                Transform attackedEntityTransform = getComponent(attackedEntityId, TransformComponent.class).getTransform();
                BasicAttackComponent attCmp = getComponent(entityId, BasicAttackComponent.class);
                EntityStateComponent stateCmp = getComponent(entityId, EntityStateComponent.class);
                boolean inAttackRange =
                    Vector3f.sub(entityTransform.getPosition(), attackedEntityTransform.getPosition(), null).length() < attCmp
                        .getBasicAttackRange();
                if (inAttackRange && (stateCmp.getState() == EntityStates.IDLING || stateCmp.getState() == EntityStates.MOVING)) {
                    fireEvent(new UpdateEntityStateEvent(entityId, EntityStates.AUTO_ATTACKING));
                    attCmp.setAttackTime(0);
                    attCmp.setDamageApplied(false);
                    attCmp.setTarget(attackedEntityId);
                } else if (!inAttackRange && hasComponent(entityId, VelocityComponent.class)) {
                    fireEvent(new UpdateEntityStateEvent(entityId, EntityStates.MOVE_TO_BASIC_ATTACK));
                    fireEvent(new MoveEntityToTargetAction(entityId, attackedEntityTransform.getPosition()));
                    attCmp.setTarget(attackedEntityId);
                } else if (!inAttackRange) {
                    fireEvent(new UpdateEntityStateEvent(entityId, EntityStates.IDLING));
                    attCmp.setTarget(null);
                }
            }
        }
        if (e instanceof MoveEntityToTargetAction) {
            String entityId = ((MoveEntityToTargetAction) e).getEntityId();
            if (hasComponent(entityId, BasicAttackComponent.class)) {
                BasicAttackComponent attCmp = getComponent(entityId, BasicAttackComponent.class);
                if (attCmp != null && attCmp.getTarget() != null) {
                    attCmp.setTarget(null);
                }
            }
        }
    }

}
