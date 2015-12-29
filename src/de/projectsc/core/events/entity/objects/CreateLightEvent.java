/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.entity.objects;

import org.lwjgl.util.vector.Vector3f;

import de.projectsc.core.data.EntityEvent;
import de.projectsc.core.data.objects.Light;

/**
 * Event for creating a new light.
 * 
 * @author Josch Bosch
 */
public class CreateLightEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = CreateLightEvent.class.getName();

    private final Light light;

    private final Vector3f position;

    public CreateLightEvent(String entity, Light light, Vector3f position) {
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
