/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.systems.physics.collision;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.component.collision.ColliderComponent;
import de.projectsc.core.component.state.EntityStateComponent;
import de.projectsc.core.data.EntityEvent;
import de.projectsc.core.data.Event;
import de.projectsc.core.data.Scene;
import de.projectsc.core.data.physics.AxisAlignedBoundingBox;
import de.projectsc.core.data.physics.WireFrame;
import de.projectsc.core.events.entity.component.ComponentAddedEvent;
import de.projectsc.core.events.entity.component.ComponentRemovedEvent;
import de.projectsc.core.events.entity.objects.NotifyEntityDeletedEvent;
import de.projectsc.core.events.entity.state.UpdateEntitySelectionEvent;
import de.projectsc.core.events.input.MouseButtonClickedAction;
import de.projectsc.core.events.input.MousePositionChangedAction;
import de.projectsc.core.events.input.MoveToPositionAction;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.interfaces.Entity;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.DefaultSystem;

/**
 * System for detecting collisisons.
 * 
 * @author Josch Bosch
 */
public class CollisionSystem extends DefaultSystem {

    private static final String NAME = "Collision System";

    private OctTree<Entity> octree;

    public CollisionSystem(EntityManager entityManager, EventManager eventManager) {
        super(CollisionSystem.NAME, entityManager, eventManager);
        eventManager.registerForEvent(ComponentAddedEvent.class, this);
        eventManager.registerForEvent(ComponentRemovedEvent.class, this);
        eventManager.registerForEvent(NotifyEntityDeletedEvent.class, this);
        eventManager.registerForEvent(MousePositionChangedAction.class, this);
        eventManager.registerForEvent(MouseButtonClickedAction.class, this);
        octree =
            new OctTree<Entity>(new AxisAlignedBoundingBox(new Vector3f(-1000, -1000, -1000), new Vector3f(1000, 1000, 1000)));

    }

    @Override
    public void update(long tick) {
        List<Entity> moved = new LinkedList<>();
        for (String e : entityManager.getAllEntites()) {
            if (hasComponent(e, ColliderComponent.class) && hasComponent(e, EntityStateComponent.class)) {
                EntityStateComponent state = getComponent(e, EntityStateComponent.class);
                if (state.isMoved()) {
                    moved.add(entityManager.getEntity(e));
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
            for (Entity entity : octree.intersectsRay(((MousePositionChangedAction) e).getCurrentRay(),
                ((MousePositionChangedAction) e).getCurrentCameraPosition())) {
                fireEvent(new UpdateEntitySelectionEvent(entity.getID(), false, true));
            }

        } else if (e instanceof MouseButtonClickedAction) {
            if (octree.intersectsRay(((MouseButtonClickedAction) e).getCurrentRay(),
                ((MouseButtonClickedAction) e).getCurrentCameraPosition()).isEmpty()) {
                if (((MouseButtonClickedAction) e).getButton() == 1) {
                    fireEvent(new MoveToPositionAction(((MouseButtonClickedAction) e).getTerrainPoint()));
                }
                // if (((MouseButtonClickedAction) e).getButton() == 0) {
                // fireEvent(new MoveToPositionAction(((MouseButtonClickedAction) e).getTerrainPoint()));
                // }
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
                ((ColliderComponent) c).update(0);
                octree.addEntity(entityManager.getEntity(e.getEntityId()));
                octree.recalculateTree();
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

    /**
     * Add debug information to scene.
     * 
     * @param scene to add to
     */
    public void debug(Scene scene) {
        List<AxisAlignedBoundingBox> boxes = octree.getBoxes();
        for (AxisAlignedBoundingBox box : boxes) {
            Vector3f size = new Vector3f(box.getSize());
            size.scale(0.5f);
            Vector3f position = new Vector3f(box.getCenter());
            position.y = position.y - size.y;
            WireFrame wf = new WireFrame(WireFrame.CUBE, position, new Vector3f(), box.getSize());
            wf.setColor(new Vector3f(0.0f, 1f, 0));
            scene.getWireFrames().add(wf);
        }
    }
}
