/*
 * Copyright (C) 2015
 */

package de.projectsc.client.core;

import java.util.Queue;

import de.projectsc.client.core.elements.Snapshot;
import de.projectsc.client.core.messages.ClientMessage;
import de.projectsc.client.core.states.ClientStates;

public interface GUI {

    boolean init();

    void render(ClientStates state, long elapsedTime, Snapshot data);

    void changeState(ClientStates state);

    void load();

    Queue<ClientMessage> readInput();

}
