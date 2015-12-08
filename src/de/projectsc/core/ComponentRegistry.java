/*
 * Copyright (C) 2015
 */

package de.projectsc.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.entities.Component;

public class ComponentRegistry {

    private static final Log LOGGER = LogFactory.getLog(ComponentRegistry.class);

    private static Map<String, Class<? extends Component>> registeredComponents = new HashMap<>();

    public static void registerComponent(String name, Class<? extends Component> componentClass) {
        if (registeredComponents.get(name) != null) {
            LOGGER.error("Component " + name + " already registered");
        } else {
            registeredComponents.put(name, componentClass);
            LOGGER.debug("Registered component " + name);
        }
    }

    public static Component createComponent(String name) {
        Class<? extends Component> componentClass = registeredComponents.get(name);
        if (componentClass != null) {
            try {
                Component c = componentClass.newInstance();
                return c;
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("Could not create component: ", e);
            }
        } else {
            LOGGER.debug("Component " + name + " is not registered.");
        }
        return null;
    }

    public static Set<String> getRegisteredComponents() {
        return registeredComponents.keySet();
    }

    public static Class<? extends Component> getComponentClass(String componentName) {
        return registeredComponents.get(componentName);
    }
}
