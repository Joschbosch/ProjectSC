/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.objects;

import de.projectsc.core.data.Event;

public class NewParticleSystemEvent extends Event {

    /**
     * ID.
     */
    public static final String ID = "NewParticelSystemEvent";

    public NewParticleSystemEvent(long entityId) {
        super(ID, entityId);

    }

}
