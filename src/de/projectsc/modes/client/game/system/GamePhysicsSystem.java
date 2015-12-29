/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.game.system;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.EntityEvent;
import de.projectsc.core.data.physics.Transform;
import de.projectsc.core.events.entities.MoveEntityToPosition;
import de.projectsc.core.events.movement.NewMovingToTargetEvent;
import de.projectsc.core.interfaces.Entity;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.physics.BasicPhysicsSystem;

public class GamePhysicsSystem extends BasicPhysicsSystem {

    public GamePhysicsSystem(EntityManager entityManager, EventManager eventManager) {
        super(entityManager, eventManager);
        eventManager.registerForEvent(MoveEntityToPosition.class, this);

    }

    @Override
    public void processEvent(EntityEvent e) {
        super.processEvent(e);
        if (e instanceof MoveEntityToPosition) {
            Entity entity = entityManager.getEntity(e.getEntityId());
            Transform t = entity.getTransform();
            Vector3f target = ((MoveEntityToPosition) e).getTarget();
            if (!t.getPosition().equals(target)) {
                fireEvent(new NewMovingToTargetEvent(entity.getID(), target));
            }
        }
    }
}
