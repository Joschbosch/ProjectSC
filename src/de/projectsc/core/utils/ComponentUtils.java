/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.utils;

import com.rits.cloning.Cloner;

import de.projectsc.core.interfaces.Component;

public final class ComponentUtils {

    private ComponentUtils() {}

    public static Component cloneComponent(Component c) {
        Component clone = Cloner.standard().deepClone(c);
        clone.createNewId();
        return clone;
    }
}
