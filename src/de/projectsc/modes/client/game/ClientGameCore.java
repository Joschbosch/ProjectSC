/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.game;

import de.projectsc.modes.client.core.ClientCore;
import de.projectsc.modes.client.core.interfaces.ClientState;
import de.projectsc.modes.client.game.states.MenuState;

public class ClientGameCore extends ClientCore {

    @Override
    protected ClientState getInitialState() {
        return new MenuState();
    }

}
