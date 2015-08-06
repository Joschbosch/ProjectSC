/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.core;

import java.util.Queue;

import de.projectsc.client.core.elements.Snapshot;
import de.projectsc.client.core.messages.ClientMessage;
import de.projectsc.client.core.states.ClientStates;

/**
 * Interface for GUI implementations.
 * 
 * @author Josch Bosch
 */
public interface GUI {

    /**
     * @return true, if successful.
     */
    boolean init();

    /**
     * Render gui.
     * 
     * @param state to render
     * @param elapsedTime since last frame
     * @param data to render
     */
    void render(ClientStates state, long elapsedTime, Snapshot data);

    /**
     * @param state to change to
     */
    void changeState(ClientStates state);

    /**
     * Load gui.
     */
    void load();

    /**
     * Read user input.
     * 
     * @return messages for core.
     */
    Queue<ClientMessage> readInput();

}
