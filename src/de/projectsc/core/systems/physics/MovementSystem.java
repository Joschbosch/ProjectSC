/*
 * Copyright (C) 2015
 */

package de.projectsc.core.systems.physics;

import java.util.Set;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.physic.MeshComponent;
import de.projectsc.core.component.physic.PathComponent;
import de.projectsc.core.component.physic.TransformComponent;
import de.projectsc.core.component.physic.VelocityComponent;
import de.projectsc.core.component.state.EntityStateComponent;
import de.projectsc.core.data.EntityEvent;
import de.projectsc.core.data.Event;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.entities.states.EntityState;
import de.projectsc.core.events.entity.actions.MoveEntityToAttackTargetEvent;
import de.projectsc.core.events.entity.actions.MoveEntityToTargetAction;
import de.projectsc.core.events.entity.movement.NotifyTransformUpdateEvent;
import de.projectsc.core.events.entity.movement.NotifyVelocityUpdateEvent;
import de.projectsc.core.events.entity.movement.UpdatePositionEvent;
import de.projectsc.core.events.entity.movement.UpdateRotationEvent;
import de.projectsc.core.events.entity.movement.UpdateScaleEvent;
import de.projectsc.core.events.entity.movement.UpdateVelocityEvent;
import de.projectsc.core.events.entity.objects.UpdateMeshEvent;
import de.projectsc.core.events.entity.state.UpdateEntityStateEvent;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.DefaultSystem;
import de.projectsc.modes.client.game.component.JumpingComponent;

/**
 * System that does all the work of positioning and moving.
 * 
 * @author Josch Bosch
 */
public class MovementSystem extends DefaultSystem {

    private static final String NAME = "Movement System";

    private static final float DISTANCE_TO_TARGET = 2f;

    public MovementSystem(EntityManager entityManager, EventManager eventManager) {
        super(MovementSystem.NAME, entityManager, eventManager);
        eventManager.registerForEvent(UpdateRotationEvent.class, this);
        eventManager.registerForEvent(UpdatePositionEvent.class, this);
        eventManager.registerForEvent(UpdateVelocityEvent.class, this);
        eventManager.registerForEvent(UpdateScaleEvent.class, this);
        eventManager.registerForEvent(UpdateMeshEvent.class, this);
        eventManager.registerForEvent(MoveEntityToTargetAction.class, this);
        eventManager.registerForEvent(MoveEntityToAttackTargetEvent.class, this);
    }

    @Override
    public void update(long tick) {
        movementSystem(tick);
        jumpingSystem(tick);

    }

    private void movementSystem(long tick) {
        Set<String> entities = entityManager.getEntitiesWithComponent(VelocityComponent.class);
        for (String entity : entities) {
            EntityState entityState = getComponent(entity, EntityStateComponent.class).getState();
            if (canMove(entityState)) {
                TransformComponent transformComp =
                    (TransformComponent) entityManager.getComponent(entity, TransformComponent.class);
                if (hasComponent(entity, PathComponent.class)) {
                    moveToTarget(tick, entity, entityState, transformComp);
                }
            }
        }
    }

    private void moveToTarget(long tick, String entity, EntityState entityState, TransformComponent transformComp) {
        PathComponent pathComponent = getComponent(entity, PathComponent.class);
        VelocityComponent velocityComp = getComponent(entity, VelocityComponent.class);
        Vector3f currentTarget = pathComponent.getCurrentTarget();
        if (currentTarget != null
            && !isAtTarget(transformComp.getTransform().getPosition(), currentTarget)) {
            if (entityState != EntityState.MOVING) {
                fireEvent(new UpdateEntityStateEvent(entity, EntityState.MOVING));
            }
            velocityComp.setCurrentSpeed(velocityComp.getMaximumSpeed());

        } else if (entityState != EntityState.IDLING && entityState != EntityState.DEAD) {
            fireEvent(new UpdateEntityStateEvent(entity, EntityState.IDLING));
            pathComponent.setCurrentTarget(null);
        }

        if (entityState == EntityState.MOVING) {
            updateVelocity(tick, transformComp.getTransform(), pathComponent, velocityComp);
            fireEvent(new NotifyVelocityUpdateEvent(entity));
            float angle = (float) Math.toDegrees(Math.atan2(velocityComp.getVelocity().x, velocityComp.getVelocity().z));
            transformComp.updatePosition(entity, velocityComp.getVelocity(), angle);
            fireEvent(new NotifyTransformUpdateEvent(entity));
        } else if (velocityComp.getCurrentSpeed() != 0 || velocityComp.getTurnSpeed() != 0) {
            velocityComp.setCurrentSpeed(0f);
            velocityComp.setTurnSpeed(0f);
            velocityComp.setVelocity(new Vector3f());
            fireEvent(new NotifyVelocityUpdateEvent(entity));
        }
    }

    private boolean canMove(EntityState entityState) {
        return entityState != EntityState.DYING && entityState != EntityState.DEAD;
    }

    private void jumpingSystem(long tick) {
        Set<String> entities = entityManager.getEntitiesWithComponent(JumpingComponent.class);
        for (String entity : entities) {
            JumpingComponent jump = getComponent(entity, JumpingComponent.class);

            float jumpTime = jump.getJumpTime();

            jumpTime += tick / 1000f * 2f;
            jumpTime %= 1;
            jump.setJumpTime(jumpTime);
            float offset = (float) Math.sin(Math.PI * 2 * jumpTime);
            offset = (offset + 1);
            if (offset > jump.getPreviousOffset()) {
                jump.setGoingUp(true);
            } else {
                jump.setGoingUp(false);
            }
            jump.setPreviousOffset(offset);
            Transform transform = getComponent(entity, TransformComponent.class).getTransform();
            transform.getPosition().y = offset;
        }
    }

    private boolean isAtTarget(Vector3f position, Vector3f target) {
        if (Vector3f.sub(position, target, null).lengthSquared() < DISTANCE_TO_TARGET) {
            return true;
        }
        return false;
    }

    @Override
    public void processEvent(Event e) {
        if (e instanceof EntityEvent) {
            processEvent((EntityEvent) e);
        }
    }

    /**
     * Process an entity event.
     * 
     * @param e to process
     */
    public void processEvent(EntityEvent e) {
        if (e instanceof UpdatePositionEvent) {
            Transform transform = getComponent(e.getEntityId(), TransformComponent.class).getTransform();
            handlePositionEvent((UpdatePositionEvent) e, transform);
        } else if (e instanceof UpdateRotationEvent) {
            Transform transform = getComponent(e.getEntityId(), TransformComponent.class).getTransform();
            handleRotateEvent((UpdateRotationEvent) e, transform);
        } else if (e instanceof UpdateScaleEvent) {
            Transform transform = getComponent(e.getEntityId(), TransformComponent.class).getTransform();
            handleScaleEvent((UpdateScaleEvent) e, transform);
        } else if (e instanceof UpdateVelocityEvent) {
            handleVelocityChangeEvent((UpdateVelocityEvent) e, getComponent(e.getEntityId(), VelocityComponent.class));
        } else if (e instanceof UpdateMeshEvent) {
            ((MeshComponent) entityManager.getComponent(e.getEntityId(), MeshComponent.class)).changeMesh(((UpdateMeshEvent) e)
                .getNewMeshFile());
        } else if (e instanceof MoveEntityToTargetAction) {
            PathComponent component = (PathComponent) entityManager.getComponent(e.getEntityId(), PathComponent.class);
            if (component != null) {
                component.setCurrentTarget(((MoveEntityToTargetAction) e).getTarget());
            }
        } else if (e instanceof MoveEntityToAttackTargetEvent) {
            PathComponent component = (PathComponent) entityManager.getComponent(e.getEntityId(), PathComponent.class);
            if (component != null) {
                component.setCurrentTarget(((MoveEntityToAttackTargetEvent) e).getTarget());
            }
        }
    }

    private void handleVelocityChangeEvent(UpdateVelocityEvent e, VelocityComponent component) {
        if (e.getAcceleration() != Float.NEGATIVE_INFINITY) {
            component.setAcceleration(e.getAcceleration());
        }
        if (e.getCurrentSpeed() != Float.NEGATIVE_INFINITY) {
            component.setCurrentSpeed(e.getCurrentSpeed());
        }
        if (e.getMaximumSpeed() != Float.NEGATIVE_INFINITY) {
            component.setMaximumSpeed(e.getMaximumSpeed());
        }
        if (e.getTurnSpeed() != Float.NEGATIVE_INFINITY) {
            component.setTurnSpeed(e.getTurnSpeed());
        }
        fireEvent(new NotifyVelocityUpdateEvent(e.getEntityId()));
    }

    private void handleScaleEvent(UpdateScaleEvent e, Transform transform) {
        if (transform.getScale() == null) {
            transform.setScale(e.getNewScale());
        } else {
            transform.getScale().set(e.getNewScale());
        }
        fireEvent(new NotifyTransformUpdateEvent(e.getEntityId()));

    }

    private void handleRotateEvent(UpdateRotationEvent e, Transform transform) {
        if (e.isRelative()) {
            Vector3f.add(transform.getRotation(), e.getNewRotation(), transform.getRotation());
        } else if (transform.getRotation() == null) {
            transform.setRotation(e.getNewRotation());
        } else {
            transform.getRotation().set(e.getNewRotation());
        }
        fireEvent(new NotifyTransformUpdateEvent(e.getEntityId()));
    }

    private void handlePositionEvent(UpdatePositionEvent e, Transform transform) {
        if (e.isRelative()) {
            Vector3f.add(transform.getPosition(), e.getNewPosition(), transform.getPosition());
        } else {
            if (transform.getPosition() == null) {
                transform.setPosition(e.getNewPosition());
            } else {
                transform.getPosition().set(e.getNewPosition());
            }
        }
        fireEvent(new NotifyTransformUpdateEvent(e.getEntityId()));
    }

    private void updateVelocity(long tick, Transform transform, PathComponent pathComponent, VelocityComponent velocityComp) {
        if (pathComponent.getCurrentTarget() != null) {
            float newSpeed = velocityComp.getCurrentSpeed() + velocityComp.getAcceleration() * tick;
            if (newSpeed >= velocityComp.getMaximumSpeed()) {
                newSpeed = velocityComp.getMaximumSpeed();
            }
            float distance = newSpeed * tick / 1000.0f;
            Vector3f currentVelocity = velocityComp.getVelocity();
            if (currentVelocity.length() == 0) {
                currentVelocity.x = (float) Math.cos(transform.getRotation().y);
                currentVelocity.z = (float) Math.sin(transform.getRotation().y);
                currentVelocity.scale(0.01f);
            }
            Vector3f desiredVelocity = Vector3f.sub(pathComponent.getCurrentTarget(), transform.getPosition(), null);
            desiredVelocity.normalise();
            desiredVelocity.scale(distance);

            Vector3f steering = Vector3f.sub(desiredVelocity, currentVelocity, null);
            steering.y = 0;
            if (velocityComp.getMaximumTurnSpeed() / steering.length() < 1) {
                steering.scale(velocityComp.getMaximumTurnSpeed() / steering.length());
            }
            Vector3f newVelocity = Vector3f.add(currentVelocity, steering, null);
            if (velocityComp.getMaximumSpeed() / newVelocity.length() < 1) {
                newVelocity.scale(velocityComp.getMaximumSpeed() / newVelocity.length());
            }
            newVelocity.y = 0;
            velocityComp.setVelocity(newVelocity);
        }
    }
}
