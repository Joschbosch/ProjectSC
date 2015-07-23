/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.core;

import au.com.ds.ef.StatefulContext;
import de.projectsc.core.game.GameConfiguration;

/**
 * Context for all game states.
 * 
 * @author Josch Bosch
 */
public class ClientGameContext extends StatefulContext {

    private final ClientCore core;

    private GUI gui;

    private GameConfiguration gameConfiguration;

    public ClientGameContext(ClientCore core) {
        this.core = core;
    }

    public ClientCore getCore() {
        return core;
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
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
