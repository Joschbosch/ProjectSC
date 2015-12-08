/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.modes.client.gui.components;

import de.projectsc.core.entities.Component;

/**
 *
 * @author Josch Bosch
 */
public enum GraphicalComponentImplementation {
    /**
     */
    EMMITING_LIGHT_COMPONENT(EmittingLightComponent.NAME, EmittingLightComponent.class),
    /**
     * 
     */
    MODEL_AND_TEXTURE_COMPONENT(ModelAndTextureComponent.NAME, ModelAndTextureComponent.class);

    private String name;

    private Class<? extends Component> clazz;

    GraphicalComponentImplementation(String name, Class<? extends Component> clazz) {
        this.setName(name);
        this.setClazz(clazz);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<? extends Component> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends Component> clazz) {
        this.clazz = clazz;
    }

}
