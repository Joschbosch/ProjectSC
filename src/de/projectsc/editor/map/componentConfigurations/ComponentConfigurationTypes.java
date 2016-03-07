/*
 * Copyright (C) 2015 
 */

package de.projectsc.editor.map.componentConfigurations;

import de.projectsc.editor.map.ComponentConfigurationPanel;
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
    PATH_POINT_COMPONENT_CONFIGURATION(PathPointComponent.NAME, PathPointComponentConfigurationPanel.class);

    private Class<? extends ComponentConfigurationPanel> clazz;

    private String componentName;

    ComponentConfigurationTypes(String componentName, Class<? extends ComponentConfigurationPanel> viewClass) {
        this.componentName = componentName;
        this.clazz = viewClass;
    }

    public String getComponentName() {
        return componentName;
    }

    public Class<? extends ComponentConfigurationPanel> getComponentClass() {
        return clazz;
    }
}
