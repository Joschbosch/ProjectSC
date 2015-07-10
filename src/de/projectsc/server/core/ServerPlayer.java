/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.server.core;

/**
 * Representation of a player in the lobby of one game and later in the game.
 * 
 * @author Josch Bosch
 */
public class ServerPlayer {

    private final AuthenticatedClient client;

    private final long id;

    public ServerPlayer(AuthenticatedClient client) {
        this.client = client;
        this.id = client.getId();
    }

    public AuthenticatedClient getClient() {
        return client;
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return client.getDisplayName();
    }
}
