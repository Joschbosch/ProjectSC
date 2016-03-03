/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.game.systems;

import java.util.Set;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.physic.TransformComponent;
import de.projectsc.core.component.state.EntityStateComponent;
import de.projectsc.core.data.Event;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.entities.states.EntityStates;
import de.projectsc.core.events.entity.actions.BasicAttackEntityAction;
import de.projectsc.core.events.entity.game.ApplyDamageEvent;
import de.projectsc.core.events.entity.state.UpdateEntityStateEvent;
import de.projectsc.core.game.components.BasicAttackComponent;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.DefaultSystem;

public class FightSystem extends DefaultSystem {

    private static final String NAME = "Fight System";

    public FightSystem(EntityManager entityManager, EventManager eventManager) {
        super(FightSystem.NAME, entityManager, eventManager);
        eventManager.registerForEvent(BasicAttackEntityAction.class, this);
    }

    @Override
    public void update(long tick) {
        Set<String> entities = entityManager.getEntitiesWithComponent(BasicAttackComponent.class);
        for (String e : entities) {
            if (hasComponent(e, EntityStateComponent.class)) {
                EntityStateComponent stateCmp = getComponent(e, EntityStateComponent.class);
                BasicAttackComponent attCmp = getComponent(e, BasicAttackComponent.class);
                if (stateCmp.getState() == EntityStates.AUTO_ATTACKING) {
                    Transform eTransform = getComponent(e, TransformComponent.class).getTransform();
                    Transform targetTransform = getComponent(attCmp.getTarget(), TransformComponent.class).getTransform();
                    EntityStateComponent targetState = getComponent(attCmp.getTarget(), EntityStateComponent.class);
                    long newAttackTime = attCmp.getAttackTime() + tick;
                    if (newAttackTime >= attCmp.getBasicAttackDamageTime() && !attCmp.isDamageApplied()) {
                        fireEvent(new ApplyDamageEvent(e, attCmp.getTarget(), attCmp.getBasicAttackDamage()));
                        attCmp.setDamageApplied(true);
                    }

                    if (newAttackTime >= attCmp.getBasicAttackDuration()) {
                        if (Vector3f.sub(eTransform.getPosition(), targetTransform.getPosition(), null).length() < attCmp
                            .getBasicAttackRange() && targetState.getState() != EntityStates.DEAD) {
                            newAttackTime -= attCmp.getBasicAttackDuration();
                            attCmp.setDamageApplied(false);
                        } else {
                            fireEvent(new UpdateEntityStateEvent(e, EntityStates.IDLING));
                        }
                    }
                    attCmp.setAttackTime(newAttackTime);
                }
            }
        }
    }

    @Override
    public void processEvent(Event e) {
        if (e instanceof BasicAttackEntityAction) {
            String entityId = ((BasicAttackEntityAction) e).getEntityId();
            String attackingEntityId = ((BasicAttackEntityAction) e).getTargetEntity();
            if (hasComponent(entityId, BasicAttackComponent.class)) {
                Transform entityTransform = getComponent(entityId, TransformComponent.class).getTransform();
                Transform attackingEntityTransform = getComponent(attackingEntityId, TransformComponent.class).getTransform();
                BasicAttackComponent attCmp = getComponent(entityId, BasicAttackComponent.class);
                EntityStateComponent stateCmp = getComponent(entityId, EntityStateComponent.class);
                if (Vector3f.sub(entityTransform.getPosition(), attackingEntityTransform.getPosition(), null).length() < attCmp
                    .getBasicAttackRange()
                    && (stateCmp.getState() == EntityStates.IDLING || stateCmp.getState() == EntityStates.MOVING)) {
                    fireEvent(new UpdateEntityStateEvent(entityId, EntityStates.AUTO_ATTACKING));
                    attCmp.setAttackTime(0);
                    attCmp.setDamageApplied(false);

                    attCmp.setTarget(attackingEntityId);
                }
            }
        }
    }

}
