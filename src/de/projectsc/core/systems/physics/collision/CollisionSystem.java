/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.systems.physics.collision;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.collision.ColliderComponent;
import de.projectsc.core.component.physic.TransformComponent;
import de.projectsc.core.component.state.EntityStateComponent;
import de.projectsc.core.data.EntityEvent;
import de.projectsc.core.data.Event;
import de.projectsc.core.data.physics.boundings.AxisAlignedBoundingBox;
import de.projectsc.core.events.entity.component.ComponentAddedEvent;
import de.projectsc.core.events.entity.component.ComponentRemovedEvent;
import de.projectsc.core.events.entity.objects.NotifyEntityDeletedEvent;
import de.projectsc.core.events.entity.state.UpdateEntitySelectionEvent;
import de.projectsc.core.events.input.MouseButtonClickedAction;
import de.projectsc.core.events.input.MousePositionChangedAction;
import de.projectsc.core.events.input.MoveToPositionAction;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.DefaultSystem;

/**
 * System for detecting collisions.
 * 
 * @author Josch Bosch
 */
public class CollisionSystem extends DefaultSystem {

    private static final String NAME = "Collision System";

    private OctTree2<String> octree;

    public CollisionSystem(EntityManager entityManager, EventManager eventManager) {
        super(CollisionSystem.NAME, entityManager, eventManager);
        eventManager.registerForEvent(ComponentAddedEvent.class, this);
        eventManager.registerForEvent(ComponentRemovedEvent.class, this);
        eventManager.registerForEvent(NotifyEntityDeletedEvent.class, this);
        eventManager.registerForEvent(MousePositionChangedAction.class, this);
        eventManager.registerForEvent(MouseButtonClickedAction.class, this);
        octree = new OctTree2<String>(new AxisAlignedBoundingBox(new Vector3f(),
            new Vector3f((float) Math.pow(2, 10), (float) Math.pow(2, 10), (float) Math.pow(2, 10))));
    }

    @Override
    public void update(long tick) {
        if (octree.isDirty()) {
            octree.recalculateTree();
        }
        List<String> moved = new LinkedList<>();
        Set<String> entities = entityManager.getEntitiesWithComponent(ColliderComponent.class);
        for (String e : entities) {
            if (hasComponent(e, EntityStateComponent.class)) {
                EntityStateComponent state = getComponent(e, EntityStateComponent.class);
                if (state.hasMoved()) {
                    moved.add(e);
                }
            }
        }
        octree.update(moved);
    }

    @Override
    public void processEvent(Event e) {
        if (e instanceof EntityEvent) {
            processEvent((EntityEvent) e);
        }

        if (e instanceof MousePositionChangedAction) {
            for (String entity : octree.intersectsRay(((MousePositionChangedAction) e).getCurrentRay(),
                ((MousePositionChangedAction) e).getCurrentCameraPosition())) {
                fireEvent(new UpdateEntitySelectionEvent(entity, false, true));
            }

        } else if (e instanceof MouseButtonClickedAction) {
            if (octree.intersectsRay(((MouseButtonClickedAction) e).getCurrentRay(),
                ((MouseButtonClickedAction) e).getCurrentCameraPosition()).isEmpty()) {
                if (((MouseButtonClickedAction) e).getButton() == 1) {
                    fireEvent(new MoveToPositionAction(((MouseButtonClickedAction) e).getTerrainPoint()));
                } else if (((MouseButtonClickedAction) e).getButton() == 0) {
                    ((MouseButtonClickedAction) e).getButton();
                    // fireEvent(new BasicAttackPoint(((MouseButtonClickedAction) e).getTerrainPoint()));
                }
            }
        }
    }

    /**
     * Process entity event.
     * 
     * @param e to process
     */
    public void processEvent(EntityEvent e) {
        if (e instanceof ComponentAddedEvent) {
            Component c = ((ComponentAddedEvent) e).getComponent();
            if (c instanceof ColliderComponent) {
                octree.addEntity(e.getEntityId(), getComponent(e.getEntityId(), TransformComponent.class).getTransform(),
                    ((ColliderComponent) c).getSimpleBoundingVolume());
            }
        } else if (e instanceof ComponentRemovedEvent) {
            Component c = ((ComponentRemovedEvent) e).getComponent();
            if (c instanceof ColliderComponent) {
                octree.removeEntity(e.getEntityId());
                octree.recalculateTree();
            }
        } else if (e instanceof NotifyEntityDeletedEvent) {
            octree.removeEntity(e.getEntityId());
        }
    }

    public OctTree2<String> getOctree() {
        return octree;
    }
}
