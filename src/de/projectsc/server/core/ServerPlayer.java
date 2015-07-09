/*
 * Copyright (C) 2015 
 */

package de.projectsc.server.core;

public class ServerPlayer {

    private AuthenticatedClient client;

    private long id;

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
