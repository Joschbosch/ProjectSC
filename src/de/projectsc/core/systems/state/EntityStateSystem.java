/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.systems.state;

import de.projectsc.core.component.state.EntityStateComponent;
import de.projectsc.core.data.EntityEvent;
import de.projectsc.core.data.Event;
import de.projectsc.core.entities.states.EntityState;
import de.projectsc.core.events.entity.state.NotifyEntitySelectionChangedEvent;
import de.projectsc.core.events.entity.state.NotifyEntityStateChangedEvent;
import de.projectsc.core.events.entity.state.UpdateEntitySelectionEvent;
import de.projectsc.core.events.entity.state.UpdateEntityStateEvent;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.DefaultSystem;

/**
 * System for all entity state changes.
 * 
 * @author Josch Bosch
 */
public class EntityStateSystem extends DefaultSystem {

    private static String NAME = "Entity State System";

    public EntityStateSystem(EntityManager entityManager, EventManager eventManager) {
        super(NAME, entityManager, eventManager);
        eventManager.registerForEvent(UpdateEntityStateEvent.class, this);
        eventManager.registerForEvent(UpdateEntitySelectionEvent.class, this);

    }

    @Override
    public void update(long tick) {

    }

    @Override
    public void processEvent(Event e) {
        if (e instanceof EntityEvent) {
            processEvent((EntityEvent) e);
        }

    }

    private void processEvent(EntityEvent e) {
        if (e instanceof UpdateEntitySelectionEvent) {
            EntityStateComponent comp = ((EntityStateComponent) entityManager.getComponent(e.getEntityId(), EntityStateComponent.class));
            comp.setEntitySelected(((UpdateEntitySelectionEvent) e).getSelected());
            comp.setHighlighted(((UpdateEntitySelectionEvent) e).isHightLighted());
            fireEvent(new NotifyEntitySelectionChangedEvent(e.getEntityId()));

        } else if (e instanceof UpdateEntityStateEvent) {
            handleChangeEntityStateEvent((UpdateEntityStateEvent) e);
        }
    }

    private void handleChangeEntityStateEvent(UpdateEntityStateEvent e) {
        EntityStateComponent component = getComponent(e.getEntityId(), EntityStateComponent.class);
        if (canChangeTo(e.getEntityState(), component)) {
            component.changeState(e.getEntityState());
            if (component.getState() == EntityState.MOVING) {
                component.setMoved(true);
            } else {
                component.setMoved(false);
            }
            fireEvent(new NotifyEntityStateChangedEvent(e.getEntityId()));
        }
    }

    private boolean canChangeTo(EntityState entityState, EntityStateComponent component) {
        return true;
    }
}
