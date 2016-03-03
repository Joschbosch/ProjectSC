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
import de.projectsc.core.entities.states.EntityStates;
import de.projectsc.core.events.entity.actions.BasicAttackEntityAction;
import de.projectsc.core.events.entity.actions.MoveEntityToTargetAction;
import de.projectsc.core.events.entity.movement.NotifyTransformUpdateEvent;
import de.projectsc.core.events.entity.state.NotifyEntityStateChangedEvent;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.DefaultSystem;

public class AISystem extends DefaultSystem {

    private static final String NAME = "AI System";

    public AISystem(EntityManager entityManager, EventManager eventManager) {
        super(NAME, entityManager, eventManager);
        eventManager.registerForEvent(NotifyTransformUpdateEvent.class, this);
        // should not be here
        eventManager.registerForEvent(NotifyEntityStateChangedEvent.class, this);
    }

    @Override
    public void update(long tick) {
        Set<String> entities = entityManager.getEntitiesWithComponent(AIControlledComponent.class);
        for (String e : entities) {
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
                OverwatchComponent owc = getComponent(e, OverwatchComponent.class);
                EntityStateComponent stateCmp = getComponent(e, EntityStateComponent.class);
                if (owc != null) {
                    if (stateCmp.getState() == EntityStates.IDLING) {
                        List<String> entitiesInRange = owc.getEntitesInRange();

                        boolean attackingEntity = false;
                        for (String targets : entitiesInRange) {
                            if (!attackingEntity && canAttack(getComponent(targets, EntityStateComponent.class).getState())) {
                                fireEvent(new BasicAttackEntityAction(e, entitiesInRange.get(0)));
                                attackingEntity = true;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean canAttack(EntityStates state) {
        return state != EntityStates.DYING && state != EntityStates.DEAD;
    }

    @Override
    public void processEvent(Event e) {
        if (e instanceof NotifyTransformUpdateEvent) {
            Set<String> entities = entityManager.getEntitiesWithComponent(OverwatchComponent.class);
            for (String ent : entities) {
                OverwatchComponent owc = getComponent(ent, OverwatchComponent.class);
                Transform t = getComponent(ent, TransformComponent.class).getTransform();
                String otherEntityId = ((NotifyTransformUpdateEvent) e).getEntityId();
                Transform other = getComponent(otherEntityId, TransformComponent.class).getTransform();
                if (Vector3f.sub(t.getPosition(), other.getPosition(), null).length() < owc.getRadius()) {
                    if (!owc.isInRange(otherEntityId)) {
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
            if (entityStateComp.getState() == EntityStates.DEAD) {
                entityManager.createNewEntityFromSchema(10000);

            }
        }
    }

}
