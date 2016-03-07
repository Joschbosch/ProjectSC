/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.game.systems;

import de.projectsc.core.data.Event;
import de.projectsc.core.entities.states.EntityState;
import de.projectsc.core.events.entity.game.ApplyDamageEvent;
import de.projectsc.core.events.entity.game.DamageTakenEvent;
import de.projectsc.core.events.entity.state.UpdateEntityStateEvent;
import de.projectsc.core.game.components.HealthComponent;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.DefaultSystem;

/**
 * System for managing the all that has to do with health of entities (also armor etc.).
 * 
 * @author Josch Bosch
 */
public class HealthSystem extends DefaultSystem {

    private static final String NAME = "Health System";

    public HealthSystem(EntityManager entityManager, EventManager eventManager) {
        super(HealthSystem.NAME, entityManager, eventManager);
        eventManager.registerForEvent(ApplyDamageEvent.class, this);
    }

    @Override
    public void update(long tick) {

    }

    @Override
    public void processEvent(Event e) {
        if (e instanceof ApplyDamageEvent) {
            ApplyDamageEvent event = (ApplyDamageEvent) e;
            String damagedEntity = event.getTarget();
            if (hasComponent(damagedEntity, HealthComponent.class)) {
                HealthComponent hCmp = getComponent(damagedEntity, HealthComponent.class);
                // apply Armor etc.
                double currentHealth = hCmp.getCurrentHealth();
                double newHealth = currentHealth - event.getDamage();
                if (newHealth <= 0) {
                    fireEvent(new UpdateEntityStateEvent(damagedEntity, EntityState.DEAD));
                } else {
                    fireEvent(new DamageTakenEvent(damagedEntity, event.getEntityId(), newHealth, event.getDamage()));
                }
                hCmp.setCurrentHealth(newHealth);
            }
        }
    }
}
