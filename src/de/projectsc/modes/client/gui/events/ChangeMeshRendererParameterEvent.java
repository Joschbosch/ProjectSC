/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui.events;

import de.projectsc.core.data.Event;

/**
 * Changes the mesh renderer parameters.
 * 
 * @author Josch Bosch
 */
public class ChangeMeshRendererParameterEvent extends Event {

    /**
     * ID.
     */
    public static final String ID = "ChangeMeshRendererParameterEvent";

    private final boolean fakeLightning;

    private final boolean transparent;

    private final float reflectivity;

    private final float shineDamper;

    private final int numColums;

    public ChangeMeshRendererParameterEvent(long entityID, boolean fakeLightning, boolean transparent, float reflectivity,
        float shineDamper, int numColums) {
        super(ID, entityID);
        this.fakeLightning = fakeLightning;
        this.transparent = transparent;
        this.reflectivity = reflectivity;
        this.shineDamper = shineDamper;
        this.numColums = numColums;
    }

    public static String getId() {
        return ID;
    }

    public boolean isFakeLightning() {
        return fakeLightning;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public int getNumColums() {
        return numColums;
    }

}
