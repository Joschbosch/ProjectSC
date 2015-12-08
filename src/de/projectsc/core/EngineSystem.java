/*
 * Copyright (C) 2015 
 */

package de.projectsc.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.data.Event;
import de.projectsc.core.entities.Component;

public abstract class EngineSystem {

    private static final Log LOGGER = LogFactory.getLog(EngineSystem.class);

    private static long idCount = 0;

    protected final long uID;

    protected final String name;

    public EngineSystem(String name) {
        this.uID = idCount++;
        this.name = name;
    }

    public abstract void processEvent(Event e);

    public abstract void update();

    public String getName() {
        return name;
    }

    public long getuID() {
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
