/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.systems;

import de.projectsc.core.systems.positioning.PhysicsSystem;

public class SystemMaster {

    private PhysicsSystem localisationSystem;

    public void initialize() {
        localisationSystem = new PhysicsSystem();
    }

    public void update() {
        localisationSystem.update();
    }

}
