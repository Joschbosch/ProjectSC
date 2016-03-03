/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.game.components;

import de.projectsc.core.CoreConstants;
import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;

public class ManaComponent extends DefaultComponent {

    /**
     * ID.
     */
    public static final String NAME = "Mana Component";

    public double maxMana = 100;

    public double currentMana = 100;

    public double currentRegenerationRate = 5; // HP/s

    public ManaComponent() {
        setComponentName(NAME);
        setType(ComponentType.GAME);
    }

    @Override
    public boolean isValidForSaving() {
        return true;
    }

    @Override
    public String serializeForNetwork() {
        return "" + currentMana + CoreConstants.SERIALIZATION_SEPARATOR + currentRegenerationRate + CoreConstants.SERIALIZATION_SEPARATOR
            + maxMana;
    }

    @Override
    public void deserializeFromNetwork(String serialized) {
        String[] values = serialized.split(CoreConstants.SERIALIZATION_SEPARATOR);
        currentMana = Double.valueOf(values[0]);
        currentRegenerationRate = Double.valueOf(values[1]);
        maxMana = Double.valueOf(values[2]);
    }

    public double getMaxHealth() {
        return maxMana;
    }

    public double getCurrentHealth() {
        return currentMana;
    }

    public void setCurrentHealth(double newHealth) {
        this.currentMana = newHealth;
    }

}
