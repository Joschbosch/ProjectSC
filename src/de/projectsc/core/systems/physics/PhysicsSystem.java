/*
 * Copyright (C) 2015
 */

package de.projectsc.core.systems.physics;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.impl.behaviour.EntityStateComponent;
import de.projectsc.core.component.impl.physic.MeshComponent;
import de.projectsc.core.component.impl.physic.TransformComponent;
import de.projectsc.core.component.impl.physic.VelocityComponent;
import de.projectsc.core.data.Event;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.entities.states.EntityState;
import de.projectsc.core.events.entities.ChangeEntityStateEvent;
import de.projectsc.core.events.entities.NewEntityCreatedEvent;
import de.projectsc.core.events.movement.ChangeMovementParameterEvent;
import de.projectsc.core.events.movement.ChangePositionEvent;
import de.projectsc.core.events.movement.ChangeRotationEvent;
import de.projectsc.core.events.movement.ChangeScaleEvent;
import de.projectsc.core.events.movement.NewPositionEvent;
import de.projectsc.core.events.objects.NewMeshEvent;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.DefaultSystem;

/**
 * System that does all the work of positioning and moving.
 * 
 * @author Josch Bosch
 */
public class PhysicsSystem extends DefaultSystem {

    private static final String NAME = "Physics System";

    public PhysicsSystem() {
        super(PhysicsSystem.NAME);
        EventManager.registerForEvent(NewEntityCreatedEvent.class, this);
        EventManager.registerForEvent(ChangeRotationEvent.class, this);
        EventManager.registerForEvent(ChangePositionEvent.class, this);
        EventManager.registerForEvent(ChangeEntityStateEvent.class, this);
        EventManager.registerForEvent(ChangeMovementParameterEvent.class, this);
        EventManager.registerForEvent(ChangeScaleEvent.class, this);
        EventManager.registerForEvent(NewMeshEvent.class, this);

    }

    @Override
    public void update(long tick) {
        for (long entity : EntityManager.getAllEntites()) {
            if (EntityManager.hasComponent(entity, EntityStateComponent.class)) {
                EntityState entityState = getComponent(entity, EntityStateComponent.class).getState();
                if (entityState == EntityState.MOVING) {
                    if (EntityManager.hasComponent(entity, VelocityComponent.class)) {
                        VelocityComponent velocityComp = getComponent(entity, VelocityComponent.class);
                        TransformComponent transform = (TransformComponent) EntityManager.getComponent(entity, TransformComponent.class);
                        velocityComp.updateVelocity(transform.getRotation());
                        Vector3f velocity = velocityComp.getVelocity();
                        Vector3f rotationDelta = velocityComp.getRotationDelta();
                        transform.updatePosition(entity, velocity, rotationDelta);
                    }
                } else if (entityState == EntityState.STANDING) {
                    if (EntityManager.hasComponent(entity, VelocityComponent.class)) {
                        VelocityComponent velocityComp = getComponent(entity, VelocityComponent.class);
                        velocityComp.setCurrentSpeed(0f);
                    }
                }
            }
        }
    }

    @Override
    public void processEvent(Event e) {
        Transform transform = EntityManager.getEntity(e.getEntityId()).getTransform();
        if (e instanceof ChangePositionEvent && transform != null) {
            handlePositionEvent((ChangePositionEvent) e, transform);
        } else if (e instanceof ChangeRotationEvent && transform != null) {
            handleRotateEvent((ChangeRotationEvent) e, transform);
        } else if (e instanceof ChangeScaleEvent && transform != null) {
            handleScaleEvent((ChangeScaleEvent) e, transform);
        } else if (e instanceof ChangeMovementParameterEvent) {
            handleVelocityChangeEvent((ChangeMovementParameterEvent) e, getComponent(e.getEntityId(), VelocityComponent.class));
        } else if (e instanceof ChangeEntityStateEvent) {
            handleChangeEntityStateEvent((ChangeEntityStateEvent) e);
        } else if (e instanceof NewEntityCreatedEvent) {
            EntityManager.addComponentToEntity(e.getEntityId(), EntityStateComponent.NAME);
        } else if (e instanceof NewMeshEvent) {
            ((MeshComponent) EntityManager.getComponent(e.getEntityId(), MeshComponent.class)).changeMesh(((NewMeshEvent) e)
                .getNewMeshFile());
        }
    }

    private void handleChangeEntityStateEvent(ChangeEntityStateEvent e) {
        if (EntityManager.hasComponent(e.getEntityId(), EntityStateComponent.class)) {
            getComponent(e.getEntityId(), EntityStateComponent.class).changeState(e.getEntityState());
        }
    }

    private void handleVelocityChangeEvent(ChangeMovementParameterEvent e, VelocityComponent component) {
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
    }

    private void handleScaleEvent(ChangeScaleEvent e, Transform transform) {
        if (transform.getScale() == null) {
            transform.setScale(e.getNewScale());
        } else {
            transform.getScale().set(e.getNewScale());
        }
        fireEvent(new NewPositionEvent(e.getEntityId(), transform.getPosition(), transform.getRotation(), transform.getScale()));

    }

    private void handleRotateEvent(ChangeRotationEvent e, Transform transform) {
        if (e.isRelative()) {
            Vector3f.add(transform.getRotation(), e.getNewRotation(), transform.getRotation());
        } else if (transform.getRotation() == null) {
            transform.setRotation(e.getNewRotation());
        } else {
            transform.getRotation().set(e.getNewRotation());
        }
        fireEvent(new NewPositionEvent(e.getEntityId(), transform.getPosition(), transform.getRotation(), transform.getScale()));
    }

    private void handlePositionEvent(ChangePositionEvent e, Transform transform) {
        if (e.isRelative()) {
            Vector3f.add(transform.getPosition(), e.getNewPosition(), transform.getPosition());
        } else {
            if (transform.getPosition() == null) {
                transform.setPosition(e.getNewPosition());
            } else {
                transform.getPosition().set(e.getNewPosition());
            }
        }
        fireEvent(new NewPositionEvent(e.getEntityId(), transform.getPosition(), transform.getRotation(), transform.getScale()));
    }

}
