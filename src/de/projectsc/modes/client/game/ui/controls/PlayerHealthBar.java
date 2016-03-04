/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.client.game.ui.controls;

import de.projectsc.core.component.state.ControlableComponent;
import de.projectsc.core.component.state.EntityStateComponent;
import de.projectsc.core.game.components.HealthComponent;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.modes.client.core.ui.UIElement;

public class PlayerHealthBar extends UIElement {

    private EntityManager entityManager;

    public PlayerHealthBar(EntityManager entityManager) {
        super("Player Health", 0);
        this.entityManager = entityManager;
    }

    public double getCurrentHealthStatus() {
        if (entityManager.getEntitiesWithComponent(ControlableComponent.class).iterator().hasNext()) {
            String entity = entityManager.getEntitiesWithComponent(ControlableComponent.class).iterator().next();
            if (entityManager.hasComponent(entity, HealthComponent.class)) {
                return ((HealthComponent) entityManager.getComponent(entity, HealthComponent.class)).getCurrentHealth();

            }
        }
        return 0;
    }

    public String getCurrentStatus() {
        if (entityManager.getEntitiesWithComponent(ControlableComponent.class).iterator().hasNext()) {
            String entity = entityManager.getEntitiesWithComponent(ControlableComponent.class).iterator().next();
            if (entityManager.hasComponent(entity, EntityStateComponent.class)) {
                return ((EntityStateComponent) entityManager.getComponent(entity, EntityStateComponent.class)).getState().toString();
            }
        }
        return "No state";

    }
}
