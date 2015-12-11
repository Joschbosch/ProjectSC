/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events;

import de.projectsc.core.data.Event;
import de.projectsc.core.data.objects.Light;

public class RemoveLightEvent extends Event {

    public static final String ID = "RemoveLightEvent";

    private final Light light;

    public RemoveLightEvent(long entity, Light light) {
        super(ID, entity);
        this.light = light;
    }

    public Light getLight() {
        return light;
    }

}
