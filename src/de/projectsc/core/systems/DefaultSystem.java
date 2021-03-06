/*
 * Copyright (C) 2015
 */

package de.projectsc.core.systems;

import java.util.UUID;

import de.projectsc.core.data.Event;
import de.projectsc.core.interfaces.Component;
import de.projectsc.core.interfaces.EngineSystem;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;

/**
 * A default imlementation of the {@link EngineSystem}. Should be used by all systems.
 * 
 * @author Josch Bosch
 */
public abstract class DefaultSystem implements EngineSystem {

    protected final String uID;

    protected final String name;

    protected EntityManager entityManager;

    private EventManager eventManager;

    public DefaultSystem(String name, EntityManager entityManager, EventManager eventManager) {
        this.uID = UUID.randomUUID().toString();
        this.name = name;
        this.entityManager = entityManager;
        this.eventManager = eventManager;

    }

    @Override
    public abstract void processEvent(Event e);

    @Override
    public String getName() {
        return name;
    }

    public String getUID() {
        return uID;
    }

    protected void fireEvent(Event e) {
        eventManager.fireEvent(e);
    }

    protected <T extends Component> T getComponent(String entityId, Class<T> componentClass) {
        Component component = entityManager.getComponent(entityId, componentClass);
        if (component != null) {
            return componentClass.cast(component);
        }
        return null;
    }

    protected boolean hasComponent(String entityId, Class<? extends Component> componentClass) {
        return entityManager.hasComponent(entityId, componentClass);
    }

    @Override
    public Class<?> getSource() {
        return this.getClass();
    }
}
