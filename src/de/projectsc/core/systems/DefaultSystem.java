/*
 * Copyright (C) 2015
 */

package de.projectsc.core.systems;

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

    private static long idCount = 0;

    protected final long uID;

    protected final String name;

    public DefaultSystem(String name) {
        this.uID = idCount++;
        this.name = name;
    }

    @Override
    public abstract void processEvent(Event e);

    @Override
    public String getName() {
        return name;
    }

    public long getUID() {
        return uID;
    }

    protected void fireEvent(Event e) {
        EventManager.fireEvent(e);
    }

    protected <T extends Component> T getComponent(long entityId, Class<T> componentClass) {
        Component component = EntityManager.getComponent(entityId, componentClass);
        if (component != null) {
            return componentClass.cast(component);
        }
        return null;
    }
}
