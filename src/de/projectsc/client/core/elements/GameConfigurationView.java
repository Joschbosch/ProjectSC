/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.client.core.elements;

import de.projectsc.core.game.GameConfiguration;

/**
 * 
 * .
 * 
 * @author Josch Bosch
 */
public class GameConfigurationView implements UIElement {

    private final GameConfiguration gameConfiguration;

    public GameConfigurationView(GameConfiguration gameConfiguration) {
        this.gameConfiguration = gameConfiguration;
    }

    public GameConfiguration getGameConfiguration() {
        return gameConfiguration;
    }

}
