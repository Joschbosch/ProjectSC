/*
 * Copyright (C) 2015
 */

package de.projectsc.core.systems.physics;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.physic.MeshComponent;
import de.projectsc.core.component.physic.PathComponent;
import de.projectsc.core.component.physic.PhysicsComponent;
import de.projectsc.core.component.physic.TransformComponent;
import de.projectsc.core.component.physic.VelocityComponent;
import de.projectsc.core.component.state.EntityStateComponent;
import de.projectsc.core.data.EntityEvent;
import de.projectsc.core.data.Event;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.entities.states.EntityState;
import de.projectsc.core.events.entity.actions.MoveEntityToTargetAction;
import de.projectsc.core.events.entity.movement.NotifyRotationTargetUpdateEvent;
import de.projectsc.core.events.entity.movement.NotifyTransformUpdateEvent;
import de.projectsc.core.events.entity.movement.NotifyVelocityUpdateEvent;
import de.projectsc.core.events.entity.movement.UpdatePositionEvent;
import de.projectsc.core.events.entity.movement.UpdateRotationEvent;
import de.projectsc.core.events.entity.movement.UpdateScaleEvent;
import de.projectsc.core.events.entity.movement.UpdateVelocityEvent;
import de.projectsc.core.events.entity.objects.UpdateMeshEvent;
import de.projectsc.core.events.entity.state.UpdateEntityStateEvent;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.DefaultSystem;
import de.projectsc.modes.client.game.component.JumpingComponent;

/**
 * System that does all the work of positioning and moving.
 * 
 * @author Josch Bosch
 */
public class BasicPhysicsSystem extends DefaultSystem {

    private static final String NAME = "Physics System";

    private static final float DISTANCE_TO_TARGET = 2f;

    public BasicPhysicsSystem(EntityManager entityManager, EventManager eventManager) {
        super(BasicPhysicsSystem.NAME, entityManager, eventManager);
        eventManager.registerForEvent(UpdateRotationEvent.class, this);
        eventManager.registerForEvent(UpdatePositionEvent.class, this);
        eventManager.registerForEvent(UpdateVelocityEvent.class, this);
        eventManager.registerForEvent(UpdateScaleEvent.class, this);
        eventManager.registerForEvent(UpdateMeshEvent.class, this);
        eventManager.registerForEvent(MoveEntityToTargetAction.class, this);
    }

    @Override
    public void update(long tick) {
        for (String entity : entityManager.getAllEntites()) {
            for (Component c : entityManager.getAllComponents(entity).values()) {
                if (c instanceof PhysicsComponent) {
                    ((PhysicsComponent) c).update(tick);
                }
            }
            EntityState entityState = getComponent(entity, EntityStateComponent.class).getState();
            TransformComponent transformComp =
                (TransformComponent) entityManager.getComponent(entity, TransformComponent.class);
            if (hasComponent(entity, VelocityComponent.class) && hasComponent(entity, PathComponent.class)) {
                PathComponent pathComponent = getComponent(entity, PathComponent.class);
                VelocityComponent velocityComp = getComponent(entity, VelocityComponent.class);
                Vector3f currentTarget = pathComponent.getCurrentTarget();
                if (currentTarget != null
                    && !isAtTarget(transformComp.getTransform().getPosition(), currentTarget)) {
                    fireEvent(new UpdateEntityStateEvent(entity, EntityState.MOVING));
                    velocityComp.setCurrentSpeed(velocityComp.getMaximumSpeed());

                    float newAngle =
                        (float) Math.atan2(currentTarget.x - transformComp.getTransform().getPosition().x, currentTarget.z
                            - transformComp.getTransform().getPosition().z);
                    newAngle = (float) (newAngle * (180 / Math.PI));
                    if (newAngle != transformComp.getRotation().y) {
                        pathComponent.setTargetRotation(new Vector3f(0, newAngle, 0));
                        velocityComp.setTurnSpeed(velocityComp.getMaximumTurnSpeed());
                        fireEvent(new NotifyRotationTargetUpdateEvent(entity));
                    }
                } else {
                    fireEvent(new UpdateEntityStateEvent(entity, EntityState.STANDING));

                }

                if (entityState == EntityState.MOVING) {
                    updateVelocity(tick, transformComp.getRotation(), pathComponent, velocityComp);
                    fireEvent(new NotifyVelocityUpdateEvent(entity));
                    transformComp.updatePosition(entity, velocityComp.getVelocity(), velocityComp.getRotationDelta());
                    fireEvent(new NotifyTransformUpdateEvent(entity));
                } else if (velocityComp.getCurrentSpeed() != 0 || velocityComp.getTurnSpeed() != 0) {
                    velocityComp.setCurrentSpeed(0f);
                    velocityComp.setTurnSpeed(0f);
                    velocityComp.setRotationDelta(new Vector3f());
                    velocityComp.setVelocity(new Vector3f());
                    fireEvent(new NotifyVelocityUpdateEvent(entity));
                }
            }
            if (hasComponent(entity, JumpingComponent.class)) {
                JumpingComponent jump = getComponent(entity, JumpingComponent.class);

                float jumpTime = jump.getJumpTime();

                jumpTime += tick / 1000f * 2f;
                jumpTime %= 1;
                jump.setJumpTime(jumpTime);
                float offset = (float) Math.sin(Math.PI * 2 * jumpTime);
                offset = (offset + 1);
                if (offset > jump.getPreviousOffset()) {
                    if (!jump.isGoingUp()) {
                        // emitter.playSound(EFFECT);
                    }
                    jump.setGoingUp(true);
                } else {
                    jump.setGoingUp(false);
                }
                jump.setPreviousOffset(offset);
                Transform transform = entityManager.getEntity(entity).getTransform();

                transform.setRotation(Vector3f.add(new Vector3f(0, tick / 1000f * 20, 0), transform.getRotation(),
                    null));
                float speed = tick / 1000f * 10;
                float dx = speed * (float) Math.sin(Math.toRadians(transform.getRotation().y - 90));
                float dz = speed * (float) Math.cos(Math.toRadians(transform.getRotation().y - 90));
                transform.getPosition().x += dx;
                transform.getPosition().z += dz;
                transform.getPosition().y = offset;
                transform.setScale(new Vector3f(0.8f, 0.8f, 0.8f));
            }
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
            Transform transform = entityManager.getEntity(e.getEntityId()).getTransform();
            handlePositionEvent((UpdatePositionEvent) e, transform);
        } else if (e instanceof UpdateRotationEvent) {
            Transform transform = entityManager.getEntity(e.getEntityId()).getTransform();
            handleRotateEvent((UpdateRotationEvent) e, transform);
        } else if (e instanceof UpdateScaleEvent) {
            Transform transform = entityManager.getEntity(e.getEntityId()).getTransform();
            handleScaleEvent((UpdateScaleEvent) e, transform);
        } else if (e instanceof UpdateVelocityEvent) {
            handleVelocityChangeEvent((UpdateVelocityEvent) e, getComponent(e.getEntityId(), VelocityComponent.class));
        } else if (e instanceof UpdateMeshEvent) {
            ((MeshComponent) entityManager.getComponent(e.getEntityId(), MeshComponent.class)).changeMesh(((UpdateMeshEvent) e)
                .getNewMeshFile());
        } else if (e instanceof MoveEntityToTargetAction) {
            PathComponent component = (PathComponent) entityManager.getComponent(e.getEntityId(), PathComponent.class);
            component.setCurrentTarget(((MoveEntityToTargetAction) e).getTarget());
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

    private void updateVelocity(long tick, Vector3f rotation, PathComponent pathComponent, VelocityComponent velocityComp) {

        float newSpeed = velocityComp.getCurrentSpeed() + velocityComp.getAcceleration() * tick;
        if (newSpeed >= velocityComp.getMaximumSpeed()) {
            newSpeed = velocityComp.getMaximumSpeed();
        }
        float distance = newSpeed * tick / 1000.0f;
        float dx = (float) (distance * Math.sin(Math.toRadians(rotation.y)));
        float dz = (float) (distance * Math.cos(Math.toRadians(rotation.y)));
        float dy = 0;
        float targetRotation = pathComponent.getTargetRotation().y;
        float angleDiff = rotation.y - targetRotation;
        if (Math.abs(angleDiff) >= 180) {
            if (rotation.y > targetRotation) {
                angleDiff = -1 * ((360 - rotation.y) + targetRotation);
            } else {
                angleDiff = (360 - targetRotation) + rotation.y;
            }
        }
        if (angleDiff > 5.E-4f) {
            if (Math.abs(angleDiff) > 180) {
                if (rotation.y > targetRotation) {
                    angleDiff = -1 * ((360 - rotation.y) + targetRotation);
                } else {
                    angleDiff = (360 - targetRotation) + rotation.y;
                }
            }
            if (angleDiff > 0) {
                dy = -velocityComp.getMaximumTurnSpeed() * tick / 1000.0f;
            } else {
                dy = velocityComp.getMaximumTurnSpeed() * tick / 1000.0f;
            }
        } else {
            dy = targetRotation - rotation.y;
        }

        velocityComp.setVelocity(new Vector3f(dx, 0, dz));
        velocityComp.setRotationDelta(new Vector3f(0, dy, 0));
    }

}
