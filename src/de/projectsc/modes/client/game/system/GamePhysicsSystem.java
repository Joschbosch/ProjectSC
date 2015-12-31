/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.game.system;

import de.projectsc.core.data.EntityEvent;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.physics.BasicPhysicsSystem;

/**
 * Game implementation of the basic physics.
 * 
 * @author Josch Bosch
 */
public class GamePhysicsSystem extends BasicPhysicsSystem {

    public GamePhysicsSystem(EntityManager entityManager, EventManager eventManager) {
        super(entityManager, eventManager);

    }

    @Override
    public void processEvent(EntityEvent e) {
        super.processEvent(e);
    }
}
