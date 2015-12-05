/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.modes.client.core;

import au.com.ds.ef.StatefulContext;
import de.projectsc.core.game.GameConfiguration;
import de.projectsc.core.modes.client.common.GUI;

/**
 * Context for all game states.
 * 
 * @author Josch Bosch
 */
public class ClientGameContext extends StatefulContext {

    private static final long serialVersionUID = -1363044117352387082L;

    private final ClientCore core;

    private GUI gui;

    private GameConfiguration gameConfiguration;

    public ClientGameContext(ClientCore core) {
        this.core = core;
    }

    public ClientCore getCore() {
        return core;
    }

    public void setGUI(GUI sgui) {
        this.gui = sgui;
    }

    public GUI getGUI() {
        return gui;
    }

    public void setGameConfiguration(GameConfiguration gameConfiguration) {
        this.gameConfiguration = gameConfiguration;
    }

    public GameConfiguration getGameConfiguration() {
        return gameConfiguration;
    }
}
