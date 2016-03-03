/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.game.components;

import de.projectsc.core.CoreConstants;
import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;

public class HealthComponent extends DefaultComponent {

    /**
     * ID.
     */
    public static final String NAME = "Health Component";

    public double maxHealth = 100;

    public double currentHealth = 100;

    public double currentRegenerationRate = 5; // HP/s

    public HealthComponent() {
        setComponentName(NAME);
        setType(ComponentType.GAME);
    }

    @Override
    public boolean isValidForSaving() {
        return true;
    }

    @Override
    public String serializeForNetwork() {
        return "" + currentHealth + CoreConstants.SERIALIZATION_SEPARATOR + currentRegenerationRate + CoreConstants.SERIALIZATION_SEPARATOR
            + maxHealth;
    }

    @Override
    public void deserializeFromNetwork(String serialized) {
        String[] values = serialized.split(CoreConstants.SERIALIZATION_SEPARATOR);
        currentHealth = Double.valueOf(values[0]);
        currentRegenerationRate = Double.valueOf(values[1]);
        maxHealth = Double.valueOf(values[2]);
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(double newHealth) {
        this.currentHealth = newHealth;
    }

}
