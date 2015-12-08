/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.modes.client.common.ui.elements;

import de.projectsc.core.game.GameConfiguration;
import de.projectsc.core.modes.client.common.data.UIElement;

/**
 * 
 * .
 * 
 * @author Josch Bosch
 */
public class GameConfigurationView extends UIElement {

    private final GameConfiguration gameConfiguration;

    public GameConfigurationView(GameConfiguration gameConfiguration) {
        super("", -1);
        this.gameConfiguration = gameConfiguration;
    }

    public GameConfiguration getGameConfiguration() {
        return gameConfiguration;
    }

}
