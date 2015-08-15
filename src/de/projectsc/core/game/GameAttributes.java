/*
 * Project SC - 2015
 * 
 * 
 */

package de.projectsc.core.game;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Attributes of an entity in the world.
 *
 * @author Josch Bosch
 */
public class GameAttributes {

    /**
     * Constant.
     */
    public static final byte AFFILIATION_NEUTRAL = 0;

    /**
     * Constant.
     */
    public static final byte AFFILIATION_DARK = 1;

    /**
     * Constant.
     */
    public static final byte AFFILIATION_LIGHT = 2;

    /**
     * Constant.
     */
    public static final String AFFILIATION = "affiliation";

    private final Map<String, Object> attributes;

    public GameAttributes() {
        this.attributes = new HashMap<>();
    }

    /**
     * Set new affiliation.
     * 
     * @param affiliation to set
     */
    public void setAffiliation(byte affiliation) {
        attributes.put(AFFILIATION, affiliation);
    }

    public byte getAffiliation() {
        return (Byte) attributes.get(AFFILIATION);
    }
}
