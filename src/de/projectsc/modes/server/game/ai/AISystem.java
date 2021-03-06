/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.server.game.ai;

import java.util.List;
import java.util.Set;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.physic.PathComponent;
import de.projectsc.core.component.physic.TransformComponent;
import de.projectsc.core.component.physic.VelocityComponent;
import de.projectsc.core.component.state.EntityStateComponent;
import de.projectsc.core.data.Event;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.entities.states.EntityState;
import de.projectsc.core.events.entity.actions.BasicAttackEntityAction;
import de.projectsc.core.events.entity.actions.MoveEntityToTargetAction;
import de.projectsc.core.events.entity.movement.NotifyTransformUpdateEvent;
import de.projectsc.core.events.entity.state.NotifyEntityStateChangedEvent;
import de.projectsc.core.events.entity.state.UpdateEntityStateEvent;
import de.projectsc.core.game.components.BasicAttackComponent;
import de.projectsc.core.game.components.HealthComponent;
import de.projectsc.core.game.components.ProjectileComponent;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.DefaultSystem;
import de.projectsc.core.systems.physics.collision.CollisionSystem;

/**
 * Main class for the AI in the game. May have sub classes (for basic AI/Game dependent AI).
 * 
 * @author Josch Bosch
 */
public class AISystem extends DefaultSystem {

    private static final String NAME = "AI System";

    public AISystem(EntityManager entityManager, EventManager eventManager, CollisionSystem collisionSystem) {
        super(NAME, entityManager, eventManager);
        eventManager.registerForEvent(NotifyTransformUpdateEvent.class, this);
        // should not be here
        eventManager.registerForEvent(NotifyEntityStateChangedEvent.class, this);
    }

    @Override
    public void update(long tick) {
        Set<String> entities = entityManager.getEntitiesWithComponent(AIControlledComponent.class);
        for (String e : entities) {
            if (hasComponent(e, FollowPathComponent.class)) {
                followPathSystem(e);
            } else {
                if (hasComponent(e, VelocityComponent.class) && hasComponent(e, PathComponent.class)) {
                    PathComponent pc = getComponent(e, PathComponent.class);
                    if (pc.getCurrentTarget() == null) {
                        Transform t = getComponent(e, TransformComponent.class).getTransform();
                        Vector3f position = t.getPosition();
                        Vector3f newPosition = new Vector3f(position);
                        newPosition.x += (2 * Math.random() - 1) * 100;
                        newPosition.z += (2 * Math.random() - 1) * 100;
                        fireEvent(new MoveEntityToTargetAction(e, newPosition));
                    }
                }
                if (hasComponent(e, OverwatchComponent.class)) {
                    checkAutoAttack(e);
                }
            }
        }
    }

    private void followPathSystem(String e) {
        FollowPathComponent fpc = getComponent(e, FollowPathComponent.class);
        Set<String> pathEntities = entityManager.getEntitiesWithComponent(PathPointComponent.class);
        boolean foundNext = false;
        for (String pathPoint : pathEntities) {
            PathPointComponent ppc = getComponent(pathPoint, PathPointComponent.class);
            if (ppc.getPathID() == fpc.getPathIDToFollow() && ppc.getGroupOrderNumber() == fpc.getNextPathPoint()) {
                foundNext = true;
                Transform followerTransform = getComponent(e, TransformComponent.class).getTransform();
                Transform pointTransform = getComponent(pathPoint, TransformComponent.class).getTransform();
                EntityStateComponent esc = getComponent(e, EntityStateComponent.class);
                if (Vector3f.sub(followerTransform.getPosition(), pointTransform.getPosition(), null).length() < 2
                    && esc.getState() == EntityState.MOVING) {
                    fireEvent(new UpdateEntityStateEvent(e, EntityState.IDLING));
                    fpc.setNextPathPoint(fpc.getNextPathPoint() + 1);
                }
                if (esc.getState() == EntityState.IDLING) {
                    fireEvent(new MoveEntityToTargetAction(e, pointTransform.getPosition()));
                }
            }
        }
        if (!foundNext) {
            fireEvent(new UpdateEntityStateEvent(e, EntityState.IDLING));
        }
    }

    private void checkAutoAttack(String e) {
        OverwatchComponent owc = getComponent(e, OverwatchComponent.class);
        EntityStateComponent stateCmp = getComponent(e, EntityStateComponent.class);
        if (owc != null) {
            if (stateCmp.getState() == EntityState.IDLING) {
                findEnemyToAttack(e, owc);
            } else if (stateCmp.getState() == EntityState.AUTO_ATTACKING) {
                BasicAttackComponent bac = getComponent(e, BasicAttackComponent.class);
                if (entityManager.getEntity(bac.getTarget()) == null
                    || getComponent(bac.getTarget(), EntityStateComponent.class).getState() == EntityState.DEAD
                    || getComponent(bac.getTarget(), EntityStateComponent.class).getState() == EntityState.DYING) {
                    owc.removeOtherEntity(bac.getTarget());
                    bac.setTarget(null);
                    fireEvent(new UpdateEntityStateEvent(e, EntityState.IDLING));

                }
            }
        }
    }

    private void findEnemyToAttack(String e, OverwatchComponent owc) {
        List<String> entitiesInRange = owc.getEntitiesInRange();
        String entityToAttack = "";
        for (String target : entitiesInRange) {
            if (hasComponent(target, HealthComponent.class)) {
                if (entityToAttack.isEmpty()) {
                    entityToAttack = target;
                } else {
                    Transform t1 = getComponent(entityToAttack, TransformComponent.class).getTransform();
                    Transform t2 = getComponent(target, TransformComponent.class).getTransform();
                    Transform source = getComponent(e, TransformComponent.class).getTransform();
                    if (Vector3f.sub(t1.getPosition(), source.getPosition(), null).lengthSquared() > Vector3f.sub(t2.getPosition(),
                        source.getPosition(), null).lengthSquared()) {
                        entityToAttack = target;
                    }
                }

            }
        }
        if (!entityToAttack.isEmpty()) {
            EntityStateComponent escTarget = getComponent(entityToAttack, EntityStateComponent.class);
            if (escTarget != null && canAttack(entityToAttack, escTarget.getState())) {
                fireEvent(new BasicAttackEntityAction(e, entityToAttack));
            }
        }
    }

    private boolean canAttack(String target, EntityState state) {
        return state != EntityState.DYING && state != EntityState.DEAD;
    }

    @Override
    public void processEvent(Event e) {
        if (e instanceof NotifyTransformUpdateEvent) {
            Set<String> entities = entityManager.getEntitiesWithComponent(OverwatchComponent.class);
            for (String ent : entities) {
                OverwatchComponent owc = getComponent(ent, OverwatchComponent.class);
                double range = owc.getRadius();
                if (owc.useBasicAttackRange()) {
                    range = getComponent(ent, BasicAttackComponent.class).getBasicAttackRange();
                }
                Transform t = getComponent(ent, TransformComponent.class).getTransform();
                String otherEntityId = ((NotifyTransformUpdateEvent) e).getEntityId();
                Transform other = getComponent(otherEntityId, TransformComponent.class).getTransform();
                if (Vector3f.sub(t.getPosition(), other.getPosition(), null).length() < range) {
                    if (!owc.isInRange(otherEntityId) && shouldNotBeWatched(otherEntityId)) {
                        owc.addEntityInRange(otherEntityId);
                    }
                } else {
                    if (owc.isInRange(otherEntityId)) {
                        owc.removeOtherEntity(otherEntityId);
                    }
                }
            }
        }
        if (e instanceof NotifyEntityStateChangedEvent) {
            String entityId = ((NotifyEntityStateChangedEvent) e).getEntityId();
            EntityStateComponent entityStateComp =
                getComponent(entityId, EntityStateComponent.class);
            if (entityStateComp.getState() == EntityState.DEAD) {
                entityManager.deleteEntity(entityId);

            }
            if (entityStateComp.getState() == EntityState.IDLING) {
                if (hasComponent(entityId, BasicAttackComponent.class)) {
                    getComponent(entityId, BasicAttackComponent.class);
                }
            }
        }
    }

    private boolean shouldNotBeWatched(String otherEntityId) {
        return !hasComponent(otherEntityId, ProjectileComponent.class);
    }

}
