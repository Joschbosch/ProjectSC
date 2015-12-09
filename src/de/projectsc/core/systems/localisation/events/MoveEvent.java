/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.systems.localisation.events;

import de.projectsc.core.data.Event;

public class MoveEvent extends Event {

    public static final String ID = "MoveEvent";

    public MoveEvent(long entityID) {
        super(ID, entityID);
    }

}
