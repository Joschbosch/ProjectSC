/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.client.game.ui.controls;

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
        if (entityManager.hasComponent("1008", HealthComponent.class)) {
            return ((HealthComponent) entityManager.getComponent("1008", HealthComponent.class)).getCurrentHealth();
        } else {
            return 0;
        }
    }

    public String getCurrentStatus() {
        if (entityManager.hasComponent("1008", EntityStateComponent.class)) {
            return ((EntityStateComponent) entityManager.getComponent("1008", EntityStateComponent.class)).getState().toString();
        } else {
            return "No state";
        }
    }
}
