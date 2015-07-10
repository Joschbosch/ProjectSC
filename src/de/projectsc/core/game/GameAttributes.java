/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.game;

import java.util.HashMap;
import java.util.Map;

public class GameAttributes {

    public static final byte AFFILIATION_NEUTRAL = 0;

    public static final byte AFFILIATION_DARK = 1;

    public static final byte AFFILIATION_LIGHT = 2;

    public static final String AFFILIATION = "affiliation";

    private Map<String, Object> attributes;

    public GameAttributes() {
        this.attributes = new HashMap<>();
    }

    public void setAffiliation(byte newAffiliation) {
        attributes.put(AFFILIATION, newAffiliation);
    }

    public byte getAffiliation() {
        return (Byte) attributes.get(AFFILIATION);
    }
}
