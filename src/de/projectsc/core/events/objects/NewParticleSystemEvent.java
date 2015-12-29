/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.objects;

import de.projectsc.core.data.EntityEvent;

public class NewParticleSystemEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = "NewParticelSystemEvent";

    public NewParticleSystemEvent(String entityId) {
        super(ID, entityId);

    }

}
