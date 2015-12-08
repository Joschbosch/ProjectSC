/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.systems.positioning;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.EngineSystem;
import de.projectsc.core.EntityManager;
import de.projectsc.core.EventManager;
import de.projectsc.core.data.Event;
import de.projectsc.core.entities.EntityState;
import de.projectsc.core.entities.components.physic.EntityStateComponent;
import de.projectsc.core.entities.components.physic.PositionComponent;
import de.projectsc.core.entities.components.physic.VelocityComponent;
import de.projectsc.core.events.ChangeEntityStateEvent;
import de.projectsc.core.events.ChangeMovementParameterEvent;
import de.projectsc.core.events.ChangePositionEvent;
import de.projectsc.core.events.MoveEvent;
import de.projectsc.core.events.NewEntityCreatedEvent;
import de.projectsc.core.events.NewPositionEvent;
import de.projectsc.core.events.RotateEvent;

/**
 * System that does all the work of positioning and moving.
 * 
 * @author Josch Bosch
 */
public class PhysicsSystem extends EngineSystem {

    private static final String NAME = "Physics System";

    private static final Log LOGGER = LogFactory.getLog(PhysicsSystem.class);

    public PhysicsSystem() {
        super(PhysicsSystem.NAME);
        EventManager.registerForEvent(ChangePositionEvent.class, this);
        EventManager.registerForEvent(RotateEvent.class, this);
        EventManager.registerForEvent(MoveEvent.class, this);
        EventManager.registerForEvent(ChangeEntityStateEvent.class, this);
        EventManager.registerForEvent(NewEntityCreatedEvent.class, this);
        EventManager.registerForEvent(ChangeMovementParameterEvent.class, this);

    }

    @Override
    public void update() {
        for (long entity : EntityManager.getAllEntites()) {
            EntityState entityState = getComponent(entity, EntityStateComponent.class).getState();
            if (entityState == EntityState.MOVING) {
                if (EntityManager.hasComponent(entity, VelocityComponent.class)
                    && EntityManager.hasComponent(entity, PositionComponent.class)) {
                    VelocityComponent velocityComp = getComponent(entity, VelocityComponent.class);
                    PositionComponent positionComp = getComponent(entity, PositionComponent.class);
                    velocityComp.updateVelocity(positionComp.getRotation());
                    Vector3f velocity = velocityComp.getVelocity();
                    Vector3f rotationDelta = velocityComp.getRotationDelta();
                    positionComp.updatePosition(entity, velocity, rotationDelta);
                }
            } else if (entityState == EntityState.STANDING) {
                if (EntityManager.hasComponent(entity, VelocityComponent.class)) {
                    VelocityComponent velocityComp = getComponent(entity, VelocityComponent.class);
                    velocityComp.setCurrentSpeed(0f);
                }
            }
        }
    }

    @Override
    public void processEvent(Event e) {
        PositionComponent posComp = getComponent(e.getEntityId(), PositionComponent.class);
        if (e instanceof ChangePositionEvent && posComp != null) {
            handlePositionEvent((ChangePositionEvent) e, posComp);
        } else if (e instanceof RotateEvent && posComp != null) {
            handleRotateEvent((RotateEvent) e, posComp);
        } else if (e instanceof ChangeMovementParameterEvent) {
            handleVelocityChangeEvent((ChangeMovementParameterEvent) e, getComponent(e.getEntityId(), VelocityComponent.class));
        } else if (e instanceof ChangeEntityStateEvent) {
            handleChangeEntityStateEvent((ChangeEntityStateEvent) e);
        } else if (e instanceof NewEntityCreatedEvent) {
            EntityManager.addComponentToEntity(e.getEntityId(), PositionComponent.NAME);
            EntityManager.addComponentToEntity(e.getEntityId(), EntityStateComponent.NAME);
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

    private void handleRotateEvent(RotateEvent e, PositionComponent posComp) {
        if (e.isRelative()) {
            Vector3f.add(posComp.getRotation(), e.getNewRotation(), posComp.getRotation());
        } else if (posComp.getRotation() == null) {
            posComp.setRotation(e.getNewRotation());
        } else {
            posComp.getRotation().set(e.getNewRotation());
        }
        fireEvent(new NewPositionEvent(e.getEntityId(), posComp.getPosition(), posComp.getRotation()));
    }

    private void handlePositionEvent(ChangePositionEvent e, PositionComponent posComp) {
        if (e.isRelative()) {
            Vector3f.add(posComp.getPosition(), e.getNewPosition(), posComp.getPosition());
        } else {
            if (posComp.getPosition() == null) {
                posComp.setPosition(e.getNewPosition());
            } else {
                posComp.getPosition().set(e.getNewPosition());
            }
        }
        fireEvent(new NewPositionEvent(e.getEntityId(), posComp.getPosition(), posComp.getRotation()));
    }

}