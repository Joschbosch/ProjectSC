/*
 * Copyright (C) 2015 
 */

package de.projectsc.editor.map.componentConfigurations;

import de.projectsc.editor.map.ComponentConfiguration;
import de.projectsc.modes.server.game.ai.PathPointComponent;

/**
 * All types of component views that exist.
 * 
 * @author Josch Bosch
 */
public enum ComponentConfigurationTypes {
    /**
     * Configuration of path points.
     */
    PATH_POINT_COMPONENT_CONFIGURATION(PathPointComponent.NAME, PathPointComponentConfiguration.class);

    private Class<? extends ComponentConfiguration> clazz;

    private String componentName;

    ComponentConfigurationTypes(String componentName, Class<? extends ComponentConfiguration> viewClass) {
        this.componentName = componentName;
        this.clazz = viewClass;
    }

    public String getComponentName() {
        return componentName;
    }

    public Class<? extends ComponentConfiguration> getComponentClass() {
        return clazz;
    }
}
