/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.entity.objects;

import de.projectsc.core.data.EntityEvent;
import de.projectsc.core.data.objects.Light;

/**
 * Event if a light should be removed.
 * 
 * @author Josch Bosch
 */
public class RemoveLightEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = RemoveLightEvent.class.getName();

    private final Light light;

    public RemoveLightEvent(String entity, Light light) {
        super(ID, entity);
        this.light = light;
    }

    public Light getLight() {
        return light;
    }

}
