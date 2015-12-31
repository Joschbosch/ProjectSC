/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.game;

import de.projectsc.modes.client.core.ClientCore;
import de.projectsc.modes.client.core.interfaces.ClientState;
import de.projectsc.modes.client.core.system.ClientControlSystem;
import de.projectsc.modes.client.game.states.MenuState;

/**
 * Game focused implementation of the {@link ClientCore}.
 * 
 * @author Josch Bosch
 */
public class ClientGameCore extends ClientCore {

    @Override
    protected ClientState getInitialState() {
        return new MenuState();
    }

    @Override
    protected void loadSystems() {
        controlSystem = new ClientControlSystem(entityManager, eventManager, networkSendQueue);
    }

}
