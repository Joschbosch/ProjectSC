/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.utils;

import com.rits.cloning.Cloner;

import de.projectsc.core.interfaces.Component;

/**
 * Utils for components.
 * 
 * @author Josch Bosch
 */
public final class ComponentUtils {

    private ComponentUtils() {}

    /**
     * Clones the given components and sets a new id.
     * 
     * @param c to clone
     * @return clone
     */
    public static Component cloneComponent(Component c) {
        Component clone = Cloner.standard().deepClone(c);
        clone.createNewId();
        return clone;
    }
}
