/*
 * Copyright (C) 2015
 */

package de.projectsc.core.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.interfaces.Component;

/**
 * The {@link ComponentManager} is a registry for all types of components. It will create instances of the components and maps their names
 * to the classes.
 * 
 * @author Josch Bosch
 */
public class ComponentManager {

    private static final Log LOGGER = LogFactory.getLog(ComponentManager.class);

    private Map<String, Class<? extends Component>> registeredComponents = new HashMap<>();

    public ComponentManager() {}

    /**
     * Register a new component class.
     * 
     * @param name of the component
     * @param componentClass of the component
     */
    public void registerComponent(String name, Class<? extends Component> componentClass) {
        if (registeredComponents.get(name) != null) {
            LOGGER.error("Component " + name + " already registered");
        } else {
            registeredComponents.put(name, componentClass);
            LOGGER.info("Registered component " + name);
        }
    }

    /**
     * Creates a new instance of the given component name.
     * 
     * @param name to create a component from.
     * @return the new component instance
     */
    public Component createComponent(String name) {
        Class<? extends Component> componentClass = registeredComponents.get(name);
        if (componentClass != null) {
            try {
                return componentClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("Could not create component: ", e);
            }
        } else {
            LOGGER.info("Component " + name + " is not registered.");
        }
        return null;
    }

    public Set<String> getRegisteredComponents() {
        return registeredComponents.keySet();
    }

    /**
     * 
     * @param componentName of a component
     * @return the class of the component if it is registered
     */
    public Class<? extends Component> getComponentClass(String componentName) {
        return registeredComponents.get(componentName);
    }

}
