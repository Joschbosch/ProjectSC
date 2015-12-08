/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.modes.client.gui.components;

import de.projectsc.core.entities.Component;

public enum GraphicalComponentImplementation {

    // BOUNDING_BOX_MODEL_COMPONENT(BoundingBoxModelComponent.NAME, BoundingBoxModelComponent.class),
    EMMITING_LIGHT_COMPONENT(EmittingLightComponent.NAME, EmittingLightComponent.class),
    MODEL_AND_TEXTURE_COMPONENT(ModelAndTextureComponent.NAME, ModelAndTextureComponent.class);

    // PARTICLE_EMITTER_COMPONENT(ParticleEmitterComponent.NAME, ParticleEmitterComponent.class);

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
