/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events.entity.objects;

import de.projectsc.core.data.EntityEvent;

/**
 * Event for adding a new particle system to the entity.
 * 
 * @author Josch Bosch
 */
public class CreateParticleSystemEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = CreateParticleSystemEvent.class.getName();

    public CreateParticleSystemEvent(String entityId) {
        super(ID, entityId);

    }

}
