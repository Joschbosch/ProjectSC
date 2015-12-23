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
import de.projectsc.core.entities.states.EntityState;
import de.projectsc.core.events.components.ComponentAddedEvent;
import de.projectsc.core.events.components.ComponentRemovedEvent;
import de.projectsc.core.events.entities.ChangeEntitySelectEvent;
import de.projectsc.core.events.entities.ChangeEntityStateEvent;
import de.projectsc.core.events.entities.DeletedEntityEvent;
import de.projectsc.core.events.entities.NewEntityCreatedEvent;
import de.projectsc.core.events.movement.ChangeMovementParameterEvent;
import de.projectsc.core.events.movement.ChangePositionEvent;
import de.projectsc.core.events.movement.ChangeRotationEvent;
import de.projectsc.core.events.movement.ChangeScaleEvent;
import de.projectsc.core.events.movement.NewPositionEvent;
import de.projectsc.core.events.objects.NewMeshEvent;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.interfaces.Entity;
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

    public PhysicsSystem(EntityManager entityManager, EventManager eventManager) {
        super(PhysicsSystem.NAME, entityManager, eventManager);
        eventManager.registerForEvent(NewEntityCreatedEvent.class, this);
        eventManager.registerForEvent(ChangeRotationEvent.class, this);
        eventManager.registerForEvent(ChangePositionEvent.class, this);
        eventManager.registerForEvent(ChangeEntityStateEvent.class, this);
        eventManager.registerForEvent(ChangeMovementParameterEvent.class, this);
        eventManager.registerForEvent(ChangeScaleEvent.class, this);
        eventManager.registerForEvent(NewMeshEvent.class, this);
        eventManager.registerForEvent(ComponentAddedEvent.class, this);
        eventManager.registerForEvent(ComponentRemovedEvent.class, this);
        eventManager.registerForEvent(DeletedEntityEvent.class, this);
        eventManager.registerForEvent(ChangeEntitySelectEvent.class, this);
        octree =
            new OctTree<Entity>(new AxisAlignedBoundingBox(new Vector3f(-1000, -1000, -1000), new Vector3f(1000, 1000, 1000)));

    }

    @Override
    public void update(long tick) {
        List<Entity> movedEntities = new LinkedList<>();
        for (long entity : entityManager.getAllEntites()) {
            for (Component c : entityManager.getAllComponents(entity).values()) {
                if (c instanceof PhysicsComponent) {
                    ((PhysicsComponent) c).update();
                }
            }
            if (entityManager.hasComponent(entity, EntityStateComponent.class)) {
                EntityState entityState = getComponent(entity, EntityStateComponent.class).getState();
                if (entityState == EntityState.MOVING) {
                    if (entityManager.hasComponent(entity, VelocityComponent.class)) {
                        VelocityComponent velocityComp = getComponent(entity, VelocityComponent.class);
                        TransformComponent transformComp =
                            (TransformComponent) entityManager.getComponent(entity, TransformComponent.class);
                        velocityComp.updateVelocity(transformComp.getRotation());
                        Vector3f velocity = velocityComp.getVelocity();
                        Vector3f rotationDelta = velocityComp.getRotationDelta();
                        transformComp.updatePosition(entity, velocity, rotationDelta);
                        movedEntities.add(entityManager.getEntity(entity));
                    }
                } else if (entityState == EntityState.STANDING) {
                    if (entityManager.hasComponent(entity, VelocityComponent.class)) {
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
        if (e instanceof ChangePositionEvent) {
            Transform transform = entityManager.getEntity(e.getEntityId()).getTransform();
            handlePositionEvent((ChangePositionEvent) e, transform);
        } else if (e instanceof ChangeRotationEvent) {
            Transform transform = entityManager.getEntity(e.getEntityId()).getTransform();
            handleRotateEvent((ChangeRotationEvent) e, transform);
        } else if (e instanceof ChangeScaleEvent) {
            Transform transform = entityManager.getEntity(e.getEntityId()).getTransform();
            handleScaleEvent((ChangeScaleEvent) e, transform);
        } else if (e instanceof ChangeMovementParameterEvent) {
            handleVelocityChangeEvent((ChangeMovementParameterEvent) e, getComponent(e.getEntityId(), VelocityComponent.class));
        } else if (e instanceof ChangeEntityStateEvent) {
            handleChangeEntityStateEvent((ChangeEntityStateEvent) e);
        } else if (e instanceof NewEntityCreatedEvent) {
            entityManager.addComponentToEntity(e.getEntityId(), EntityStateComponent.NAME);
        } else if (e instanceof ComponentAddedEvent) {
            Component c = ((ComponentAddedEvent) e).getComponent();
            if (c instanceof ColliderComponent) {
                ((ColliderComponent) c).update();
                octree.addEntity(entityManager.getEntity(e.getEntityId()));
                octree.recalculateTree();
            }
        } else if (e instanceof ComponentRemovedEvent) {
            Component c = ((ComponentRemovedEvent) e).getComponent();
            if (c instanceof ColliderComponent) {
                octree.removeEntity(e.getEntityId());
                octree.recalculateTree();
            }
        } else if (e instanceof NewMeshEvent) {
            ((MeshComponent) entityManager.getComponent(e.getEntityId(), MeshComponent.class)).changeMesh(((NewMeshEvent) e)
                .getNewMeshFile());
        } else if (e instanceof DeletedEntityEvent) {
            octree.removeEntity(e.getEntityId());
        } else if (e instanceof ChangeEntitySelectEvent) {
            EntityStateComponent comp = ((EntityStateComponent) entityManager.getComponent(e.getEntityId(), EntityStateComponent.class));
            comp.setEntitySelected(((ChangeEntitySelectEvent) e).getSelected());
            comp.setHighlighted(((ChangeEntitySelectEvent) e).isHightLighted());
        }
    }

    private void handleChangeEntityStateEvent(ChangeEntityStateEvent e) {
        if (entityManager.hasComponent(e.getEntityId(), EntityStateComponent.class)) {
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

        for (long entity : entityManager.getAllEntites()) {
            for (Component c : entityManager.getAllComponents(entity).values()) {
                if (c instanceof ColliderComponent) {
                    AxisAlignedBoundingBox box = ((ColliderComponent) c).getAABB();
                    WireFrame wf =
                        new WireFrame(WireFrame.CUBE, entityManager.getEntity(entity).getTransform().getPosition(), new Vector3f(),
                            box.getSize());
                    wf.setColor(new Vector3f(1.0f, 0, 0));
                    s.getWireFrames().add(wf);
                }
            }
        }

    }

}
