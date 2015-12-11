/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.Event;
import de.projectsc.core.data.objects.Light;

public class CreateNewLightEvent extends Event {

    public static final String ID = "CreateNewLightEvent";

    private final Light light;

    private final Vector3f position;

    public CreateNewLightEvent(long entity, Light light, Vector3f position) {
        super(ID, entity);
        this.light = light;
        this.position = position;
    }

    public Light getLight() {
        return light;
    }

    public Vector3f getPosition() {
        return position;
    }

}
