/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.game.components;

import de.projectsc.core.CoreConstants;
import de.projectsc.core.component.ComponentType;
import de.projectsc.core.component.DefaultComponent;
import de.projectsc.core.interfaces.Component;

public class ManaComponent extends DefaultComponent {

    /**
     * ID.
     */
    public static final String NAME = "Mana Component";

    public double maxMana = 100;

    public double currentMana = 100;

    public double currentRegenerationRate = 5; // Mana/s

    public ManaComponent() {
        setComponentName(NAME);
        setType(ComponentType.GAME);
    }

    @Override
    public String serializeForNetwork() {
        return "" + currentMana + CoreConstants.SERIALIZATION_SEPARATOR + currentRegenerationRate + CoreConstants.SERIALIZATION_SEPARATOR
            + maxMana;
    }

    @Override
    public Component cloneComponent() {
        ManaComponent mc = new ManaComponent();
        mc.setMaxMana(maxMana);
        mc.setCurrentRegenerationRate(currentRegenerationRate);
        return mc;

    }

    @Override
    public void deserializeFromNetwork(String serialized) {
        String[] values = serialized.split(CoreConstants.SERIALIZATION_SEPARATOR);
        currentMana = Double.valueOf(values[0]);
        currentRegenerationRate = Double.valueOf(values[1]);
        maxMana = Double.valueOf(values[2]);
    }

    public void setMaxMana(double maxMana) {
        this.maxMana = maxMana;
    }

    public double getMaxMana() {
        return maxMana;
    }

    public double getCurrentMana() {
        return currentMana;
    }

    public void setCurrentRegenerationRate(double currentRegenerationRate) {
        this.currentRegenerationRate = currentRegenerationRate;
    }

    public void setCurrentMana(double newMana) {
        this.currentMana = newMana;
    }

}
