/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.entities.components;

import de.projectsc.core.entities.Component;
import de.projectsc.core.entities.components.physic.EntityStateComponent;
import de.projectsc.core.entities.components.physic.PositionComponent;
import de.projectsc.core.entities.components.physic.VelocityComponent;

public enum ComponentListItem {

    POSITION_COMPONENT(PositionComponent.NAME, PositionComponent.class),
    VELOCITY_COMPONENT(VelocityComponent.NAME, VelocityComponent.class),
    ENTITY_STATE_COMPONENT(EntityStateComponent.NAME, EntityStateComponent.class);

    private String name;

    private Class<? extends Component> clazz;

    ComponentListItem(String name, Class<? extends Component> clazz) {
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
