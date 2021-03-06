/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.gui.components;

import de.projectsc.core.component.DefaultComponent;

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
    MODEL_AND_TEXTURE_COMPONENT(MeshRendererComponent.NAME, MeshRendererComponent.class),
    /**
     * 
     */
    PARTICLE_SYSTEM_COMPONENT(ParticleSystemComponent.NAME, ParticleSystemComponent.class);

    private String name;

    private Class<? extends DefaultComponent> clazz;

    GraphicalComponentImplementation(String name, Class<? extends DefaultComponent> clazz) {
        this.setName(name);
        this.setClazz(clazz);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<? extends DefaultComponent> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends DefaultComponent> clazz) {
        this.clazz = clazz;
    }

}
