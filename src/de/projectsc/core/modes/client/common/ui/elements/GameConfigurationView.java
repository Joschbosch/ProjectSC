/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.modes.client.common.ui.elements;

import de.projectsc.core.game.GameConfiguration;

/**
 * 
 * .
 * 
 * @author Josch Bosch
 */
public class GameConfigurationView extends UIElement {

    private final GameConfiguration gameConfiguration;

    public GameConfigurationView(GameConfiguration gameConfiguration) {
        this.gameConfiguration = gameConfiguration;
    }

    public GameConfiguration getGameConfiguration() {
        return gameConfiguration;
    }

}
