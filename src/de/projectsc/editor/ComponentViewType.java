/*
 * Copyright (C) 2015 
 */

package de.projectsc.editor;

import de.projectsc.core.entities.components.physic.VelocityComponent;
import de.projectsc.core.modes.client.gui.components.EmittingLightComponent;
import de.projectsc.editor.componentViews.EmittingLightComponentView;
import de.projectsc.editor.componentViews.MovingComponentView;

/**
 * All types of component views that exist.
 * 
 * @author Josch Bosch
 */
public enum ComponentViewType {
    /**
     * View of velocity component.
     */
    MOVING_COMPONENT_VIEW(VelocityComponent.NAME, MovingComponentView.class),
    /**
     * View of light component.
     */
    EMMITING_LIGHT_COMPONENT_VIEW(EmittingLightComponent.NAME, EmittingLightComponentView.class);

    private Class<? extends ComponentView> clazz;

    private String componentName;

    ComponentViewType(String componentName, Class<? extends ComponentView> viewClass) {
        this.componentName = componentName;
        this.clazz = viewClass;
    }

    public String getComponentName() {
        return componentName;
    }

    public Class<? extends ComponentView> getComponentClass() {
        return clazz;
    }
}
