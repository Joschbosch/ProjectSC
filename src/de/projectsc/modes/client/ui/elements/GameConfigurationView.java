/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.ui.elements;

import de.projectsc.core.game.GameConfiguration;
import de.projectsc.modes.client.ui.BasicUIElement;

/**
 * 
 * .
 * 
 * @author Josch Bosch
 */
public class GameConfigurationView extends BasicUIElement {

    private final GameConfiguration gameConfiguration;

    public GameConfigurationView(GameConfiguration gameConfiguration) {
        super("", -1);
        this.gameConfiguration = gameConfiguration;
    }

    public GameConfiguration getGameConfiguration() {
        return gameConfiguration;
    }

}
