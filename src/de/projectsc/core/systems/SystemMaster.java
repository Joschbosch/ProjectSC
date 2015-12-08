/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.systems;

import de.projectsc.core.systems.localisation.LocalisationSystem;

public class SystemMaster {

    private LocalisationSystem localisationSystem;

    public void initialize() {

        localisationSystem = new LocalisationSystem();
    }
}
