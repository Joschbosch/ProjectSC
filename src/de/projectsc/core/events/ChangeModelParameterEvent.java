/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events;

import de.projectsc.core.data.Event;

public class ChangeModelParameterEvent extends Event {

    public static final String ID = "ChangeScaleEvent";

    private final float newScale;

    private boolean fakeLightning;

    private boolean transparent;

    private float reflectivity;

    private float shineDamper;

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

    private int numColums;

    public ChangeModelParameterEvent(long entityID, float newScale, boolean fakeLightning, boolean transparent, float reflectivity,
        float shineDamper, int numColums) {
        super(ID, entityID);
        this.newScale = newScale;
        this.fakeLightning = fakeLightning;
        this.transparent = transparent;
        this.reflectivity = reflectivity;
        this.shineDamper = shineDamper;
        this.numColums = numColums;
    }

    public float getNewScale() {
        return newScale;
    }

}
