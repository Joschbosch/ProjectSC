/*
 * Copyright (C) 2015
 */

package de.projectsc.core.systems.physics;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.impl.behaviour.EntityStateComponent;
import de.projectsc.core.component.impl.physic.ColliderComponent;
import de.projectsc.core.component.impl.physic.MeshComponent;
import de.projectsc.core.component.impl.physic.PhysicsComponent;
import de.projectsc.core.component.impl.physic.TransformComponent;
import de.projectsc.core.component.impl.physic.VelocityComponent;
import de.projectsc.core.data.Event;
import de.projectsc.core.data.Scene;
import de.projectsc.core.data.physics.AxisAlignedBoundingBox;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.data.physics.WireFrame;
import de.projectsc.core.data.structure.OctTree;
import de.projectsc.core.entities.Entity;
import de.projectsc.core.entities.states.EntityState;
import de.projectsc.core.events.components.ComponentAddedEvent;
import de.projectsc.core.events.components.ComponentRemovedEvent;
import de.projectsc.core.events.entities.ChangeEntityStateEvent;
import de.projectsc.core.events.entities.NewEntityCreatedEvent;
import de.projectsc.core.events.movement.ChangeMovementParameterEvent;
import de.projectsc.core.events.movement.ChangePositionEvent;
import de.projectsc.core.events.movement.ChangeRotationEvent;
import de.projectsc.core.events.movement.ChangeScaleEvent;
import de.projectsc.core.events.movement.NewPositionEvent;
import de.projectsc.core.events.objects.NewMeshEvent;
import de.projectsc.core.interfaces.Component;
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

    private OctTree<Entity> octree;

    public PhysicsSystem() {
        super(PhysicsSystem.NAME);
        EventManager.registerForEvent(NewEntityCreatedEvent.class, this);
        EventManager.registerForEvent(ChangeRotationEvent.class, this);
        EventManager.registerForEvent(ChangePositionEvent.class, this);
        EventManager.registerForEvent(ChangeEntityStateEvent.class, this);
        EventManager.registerForEvent(ChangeMovementParameterEvent.class, this);
        EventManager.registerForEvent(ChangeScaleEvent.class, this);
        EventManager.registerForEvent(NewMeshEvent.class, this);
        EventManager.registerForEvent(ComponentAddedEvent.class, this);
        EventManager.registerForEvent(ComponentRemovedEvent.class, this);

        octree = new OctTree<Entity>(new AxisAlignedBoundingBox(new Vector3f(-500, -500, -500), new Vector3f(500, 500, 500)));

    }

    @Override
    public void update(long tick) {
        List<Entity> movedEntities = new LinkedList<>();
        for (long entity : EntityManager.getAllEntites()) {
            for (Component c : EntityManager.getAllComponents(entity).values()) {
                if (c instanceof PhysicsComponent) {
                    ((PhysicsComponent) c).update(entity);
                }
            }
            if (EntityManager.hasComponent(entity, EntityStateComponent.class)) {
                EntityState entityState = getComponent(entity, EntityStateComponent.class).getState();
                if (entityState == EntityState.MOVING) {
                    if (EntityManager.hasComponent(entity, VelocityComponent.class)) {
                        VelocityComponent velocityComp = getComponent(entity, VelocityComponent.class);
                        TransformComponent transformComp =
                            (TransformComponent) EntityManager.getComponent(entity, TransformComponent.class);
                        velocityComp.updateVelocity(transformComp.getRotation());
                        Vector3f velocity = velocityComp.getVelocity();
                        Vector3f rotationDelta = velocityComp.getRotationDelta();
                        transformComp.updatePosition(entity, velocity, rotationDelta);
                        movedEntities.add(EntityManager.getEntity(entity));
                    }
                } else if (entityState == EntityState.STANDING) {
                    if (EntityManager.hasComponent(entity, VelocityComponent.class)) {
                        VelocityComponent velocityComp = getComponent(entity, VelocityComponent.class);
                        velocityComp.setCurrentSpeed(0f);
                    }
                }
            }
        }
        octree.update(movedEntities);
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
        } else if (e instanceof ComponentAddedEvent) {
            Component c = ((ComponentAddedEvent) e).getComponent();
            if (c instanceof ColliderComponent) {
                ((ColliderComponent) c).update(e.getEntityId());
                octree.addEntity(EntityManager.getEntity(e.getEntityId()));
                octree.recalculateTree();
            }
        } else if (e instanceof ComponentRemovedEvent) {
            Component c = ((ComponentRemovedEvent) e).getComponent();
            if (c instanceof ColliderComponent) {
                octree.removeEntity(EntityManager.getEntity(e.getEntityId()));
                octree.recalculateTree();
            }
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

    public void debug(Scene s) {
        List<AxisAlignedBoundingBox> boxes = octree.getBoxes();
        for (AxisAlignedBoundingBox box : boxes) {
            Vector3f size = new Vector3f(box.getSize());
            size.scale(0.5f);
            Vector3f position = new Vector3f(box.getCenter());
            position.y = position.y - size.y;
            WireFrame wf = new WireFrame(WireFrame.CUBE, position, new Vector3f(), box.getSize());
            wf.setColor(new Vector3f(0.0f, 1f, 0));
            s.getWireFrames().add(wf);
        }

        for (long entity : EntityManager.getAllEntites()) {
            for (Component c : EntityManager.getAllComponents(entity).values()) {
                if (c instanceof ColliderComponent) {
                    AxisAlignedBoundingBox box = ((ColliderComponent) c).getAABB();
                    WireFrame wf =
                        new WireFrame(WireFrame.CUBE, EntityManager.getEntity(entity).getTransform().getPosition(), new Vector3f(),
                            box.getSize());
                    wf.setColor(new Vector3f(1.0f, 0, 0));
                    s.getWireFrames().add(wf);
                }
            }
        }

    }

}
